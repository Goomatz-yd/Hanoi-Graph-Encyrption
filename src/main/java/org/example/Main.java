package org.example;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Path CONFIG_INPUT = Paths.get("config.txt");

    // File paths for encryption
    private static final Path TEXT_INPUT = Paths.get("text-input.txt");
    private static final Path CIPHER_OUTPUT = Paths.get("cipher-output.bin");
    private static final Path KEY_OUTPUT = Paths.get("key-output.txt");

    // File paths for decryption
    private static final Path CIPHER_INPUT = Paths.get("cipher-input.bin");
    private static final Path KEY_INPUT = Paths.get("key-input.txt");
    private static final Path TEXT_OUTPUT = Paths.get("text-output.txt");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        System.out.println("=== TOWERS OF HANOI STREAM CIPHER ===");

        while(run) {
            int[] config = loadConfig();
            if (config == null) return;
            int r = config[0];
            int l = config[1];

            System.out.println("\nLoaded Configuration -> " + r + " Rings & " + l + " Moves");
            System.out.println("1. Encrypt (Reads text-input.txt)");
            System.out.println("2. Decrypt (Reads cipher-input.bin & key-input.txt)");
            System.out.println("3. Simulate Transmission (Safely copies Output files to Input files)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Clear buffer

            try {
                switch (choice) {
                    case 0: run = false; break;
                    case 1: encryptFlow(r, l); break;
                    case 2: decryptFlow(r); break;
                    case 3: simulateTransmission(); break;
                    default: System.out.println("Invalid choice."); break;
                }
            } catch (Exception e) {
                System.err.println("An error occurred during file operations: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    private static void simulateTransmission() throws IOException {
        System.out.println("\n--- SIMULATING FILE TRANSMISSION ---");
        if (Files.exists(CIPHER_OUTPUT)) {
            Files.copy(CIPHER_OUTPUT, CIPHER_INPUT, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Safely copied " + CIPHER_OUTPUT.getFileName() + " to " + CIPHER_INPUT.getFileName());
        } else {
            System.out.println("Error: " + CIPHER_OUTPUT.getFileName() + " not found. Run Encrypt first.");
        }

        if (Files.exists(KEY_OUTPUT)) {
            Files.copy(KEY_OUTPUT, KEY_INPUT, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Safely copied " + KEY_OUTPUT.getFileName() + " to " + KEY_INPUT.getFileName());
        } else {
            System.out.println("Error: " + KEY_OUTPUT.getFileName() + " not found. Run Encrypt first.");
        }
    }

    private static void encryptFlow(int r, int l) throws IOException, InterruptedException {
        if (!Files.exists(TEXT_INPUT)) {
            System.out.println("Error: " + TEXT_INPUT.getFileName() + " not found! Please create it and add your secret text.");
            return;
        }

        byte[] messageBytes = Files.readAllBytes(TEXT_INPUT);
        System.out.println("\nRead " + messageBytes.length + " bytes from " + TEXT_INPUT.getFileName());

        CipherResult result = EncryptionEngine.encrypt(messageBytes, r, l);

        Files.write(CIPHER_OUTPUT, result.ciphertext);

        String keyData = result.seed.toString() + "\n" + messageBytes.length;
        Files.writeString(KEY_OUTPUT, keyData);

        System.out.println("\nSuccess! Your files have been separated for security:");
        System.out.println("1. Ciphertext (Raw Binary) -> " + CIPHER_OUTPUT.getFileName());
        System.out.println("2. Public Seed (Text)      -> " + KEY_OUTPUT.getFileName());
    }

    private static void decryptFlow(int r) throws IOException {
        if (!Files.exists(CIPHER_INPUT) || !Files.exists(KEY_INPUT)) {
            System.out.println("Error: Missing input files. Use Option 3 to move them.");
            return;
        }

        System.out.println("\nFound " + CIPHER_INPUT.getFileName() + " and " + KEY_INPUT.getFileName());

        byte[] cipherBytes = Files.readAllBytes(CIPHER_INPUT);
        String keyFileContent = Files.readString(KEY_INPUT).trim();
        String[] tokens = keyFileContent.split("\\s+");

        if (tokens.length < 2) {
            System.out.println("Error: Key file is missing the length parameter. Re-encrypt the file.");
            return;
        }

        BigInteger seed = new BigInteger(tokens[0]);
        int originalLength = Integer.parseInt(tokens[1]);

        System.out.println("Authorizing Seed... Decrypting data...");

        if (cipherBytes.length != originalLength) {
            System.out.println("WARNING: File size corruption detected! Attempting math recovery...");
            byte[] restoredBytes = new byte[originalLength];
            System.arraycopy(cipherBytes, 0, restoredBytes, 0, Math.min(cipherBytes.length, originalLength));
            cipherBytes = restoredBytes;
        }

        byte[] decryptedBytes = EncryptionEngine.decrypt(r, cipherBytes, seed);
        Files.write(TEXT_OUTPUT, decryptedBytes);

        System.out.println("\n--- DECRYPTION COMPLETE ---");
        System.out.println("Decrypted data successfully restored to: " + TEXT_OUTPUT.getFileName());
    }

    private static int[] loadConfig() {
        try {
            if (!Files.exists(CONFIG_INPUT)) {
                String defaultConfig = "r=5\nl=5\n";
                Files.writeString(CONFIG_INPUT, defaultConfig);
                System.out.println("Created default " + CONFIG_INPUT.getFileName() + " file.");
                return new int[]{5, 5};
            }

            List<String> lines = Files.readAllLines(CONFIG_INPUT);
            int r = 5, l = 5;

            for (String line : lines) {
                line = line.trim().toLowerCase();
                if (line.startsWith("r=")) r = Integer.parseInt(line.substring(2));
                if (line.startsWith("l=")) l = Integer.parseInt(line.substring(2));
            }
            return new int[]{r, l};

        } catch (Exception e) {
            System.out.println("Error reading " + CONFIG_INPUT.getFileName());
            return null;
        }
    }
}