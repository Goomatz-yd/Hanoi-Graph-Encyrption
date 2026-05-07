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

        System.out.println("towers of hanoi based stream cipher with utilization of recursive graph search and k long conjectures - by Yiftah David");

        while(run) {
            int[] config = loadConfig();
            if (config == null) return;
            int r = config[0];
            int l = config[1];

            System.out.println("\nLoaded Configuration -> " + r + " Rings & " + l + " Moves");
            System.out.println("1. Encrypt (Requires text-input.txt)");
            System.out.println("2. Decrypt (Requires cipher-input.bin & key-input.txt)");
            System.out.println("3. Simulate Transmission (Safely copies Output files to Input files)");
            System.out.println("0. Safely clear IO files & exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 0: run = clearFiles(); break;
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
    //safely closes the session by clearing files and not leaving garbage
    private static boolean clearFiles() throws IOException {
        Files.write(TEXT_OUTPUT, "".getBytes());
        Files.write(CIPHER_OUTPUT, "".getBytes());
        Files.write(KEY_OUTPUT, "".getBytes());
        Files.write(TEXT_INPUT, "".getBytes());
        Files.write(CIPHER_INPUT, "".getBytes());
        Files.write(KEY_INPUT, "".getBytes());
        return false;
    }

    //uses to simulate a transfer of the O to the I via hardcopy, since copy-paste fucks it up
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
    //dictates the flow of encryption, when not only encrypts, handles the IO UI around the encryption
    private static void encryptFlow(int r, int l) throws IOException, InterruptedException {
        if (!Files.exists(TEXT_INPUT)) {
            System.out.println("text for input missing");
            return;
        }

        byte[] messageBytes = Files.readAllBytes(TEXT_INPUT);
        System.out.println("\nread " + messageBytes.length + " bytes from " + TEXT_INPUT.getFileName());

        CipherResult result = EncryptionEngine.encrypt(messageBytes, r, l);

        Files.write(CIPHER_OUTPUT, result.ciphertext);

        String keyData = result.seed.toString() + "\n" + messageBytes.length;
        Files.writeString(KEY_OUTPUT, keyData);

        System.out.println("\nencryption successful, output files:");
        System.out.println("1. ciphertext (bin) -> " + CIPHER_OUTPUT.getFileName());
        System.out.println("2. public Seed (yext)      -> " + KEY_OUTPUT.getFileName());
    }
    //dictates the flow of decryption, when not only decrypts, handles the IO UI around the decryption
    private static void decryptFlow(int r) throws IOException {
        if (!Files.exists(CIPHER_INPUT) || !Files.exists(KEY_INPUT)) {
            System.out.println("missing input files");
            return;
        }

        System.out.println("\nfound Input files");

        byte[] cipherBytes = Files.readAllBytes(CIPHER_INPUT);
        String keyFileContent = Files.readString(KEY_INPUT).trim();
        String[] tokens = keyFileContent.split("\\s+");

        if (tokens.length < 2) {
            System.out.println("key file missing length parameter");
            return;
        }

        BigInteger seed = new BigInteger(tokens[0]);
        int originalLength = Integer.parseInt(tokens[1]);

        if (cipherBytes.length != originalLength) {
            System.out.println("file size corruption detected, decryption may fail");
            byte[] restoredBytes = new byte[originalLength];
            System.arraycopy(cipherBytes, 0, restoredBytes, 0, Math.min(cipherBytes.length, originalLength));
            cipherBytes = restoredBytes;
        }

        byte[] decryptedBytes = EncryptionEngine.decrypt(r, cipherBytes, seed);
        Files.write(TEXT_OUTPUT, decryptedBytes);

        System.out.println("decryption successful, output file:");
        System.out.println(TEXT_OUTPUT.getFileName());
    }
    //loads configuration(num of rings in game, num of moves for player on encryption)
    private static int[] loadConfig() {
        try {
            if (!Files.exists(CONFIG_INPUT)) {
                String defaultConfig = "r=5\nl=5\n";
                Files.writeString(CONFIG_INPUT, defaultConfig);
                System.out.println("created default config");
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
            System.out.println("error reading " + CONFIG_INPUT.getFileName());
            return null;
        }
    }
}