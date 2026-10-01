# CipheRaz — Educational Cryptography & Steganography Laboratory
**Developed by Abderrazak BENAROUS**

An advanced, cross-platform educational desktop workstation designed for university coursework, lecture demonstrations, and student research in:
* **Cryptography & Cryptanalysis**
* **Network & Information Security**
* **Digital Forensics & Steganography**
* **Cybersecurity Engineering**

CipheRaz is inspired by the educational concept of interactive security laboratories, but is an **entirely independent, clean-room implementation** featuring an original **Cybersecurity Research Laboratory** design, high-contrast dark theme, and rich internal state visualizers.

---

## 🔬 Core Mission & Educational Philosophy

Traditional tools often reduce cryptographic algorithms to black-box "Encrypt" and "Decrypt" buttons. CipheRaz is engineered from the ground up to illuminate what happens *under the hood*:

1. **Internal State Visibility**: Exposes intermediate matrix transformations, S-Boxes, modular arithmetic steps, and bit-level pixel modifications.
2. **Honest Security Advisories**: Prominent colored banners clearly delineate between historical toy ciphers (Caesar, Playfair, toy RSA) and modern authenticated standards (AES-256-GCM, SHA-256).
3. **Interactive Cryptanalysis**: Empowers students to break historical ciphers using real-time character frequency histograms, Index of Coincidence (IoC) metrics, and Chi-Square automated brute-force attacks.
4. **Bit-Level Steganography & Steganalysis**: Enables lossless PNG/BMP data concealment with selectable bit-depth (1–4 bpc), difference maps, PSNR/MSE fidelity calculations, AES-GCM password protection, and bit-plane visual filtering.
5. **100% Offline & Private**: Zero telemetry, zero analytics, zero external network traffic. Sensitive keys and payloads are never persisted to disk by default.

---

## 🛠️ Technology & Dependency Stack

* **Language**: Java 21 LTS
* **GUI Framework**: JavaFX 21 (`javafx-controls`, `javafx-fxml`, `javafx-graphics`, `javafx-swing`)
* **Styling**: Custom CSS (`dark-lab.css`) with WCAG AAA/AA high contrast
* **Vector Icons**: Resolution-independent inline SVG vector paths (`SvgIcons.java`)
* **Cryptography**: Standard Java Cryptography Architecture (JCA) + custom mathematical engines
* **Database**: Embedded SQLite (`sqlite-jdbc`) for session logs and coursework progress
* **Testing**: JUnit 5 Jupiter + AssertJ
* **Logging**: SLF4J + Logback (with strict privacy masking)
* **Build System**: Apache Maven (`./mvnw` self-contained wrapper)

---

## 📁 System Architecture & Project Layout

```text
CipheRaz/
├── pom.xml                               # Maven project definition
├── mvnw / mvnw.cmd                       # Maven wrapper
├── package.sh                            # Native jpackage bundling script
├── README.md                             # Comprehensive technical manual
├── src/
│   ├── main/
│   │   ├── java/com/cryptolab/
│   │   │   ├── app/                      # Application lifecycle & navigation
│   │   │   │   ├── ApplicationLauncher.java
│   │   │   │   ├── CryptoLabApp.java
│   │   │   │   ├── AppContext.java
│   │   │   │   └── NavigationManager.java
│   │   │   ├── core/                     # Pure cryptographic & mathematical engines
│   │   │   │   ├── model/                # Category, Difficulty, Result, Status enums
│   │   │   │   ├── classical/            # Caesar, Atbash, Affine, Vigenère, Playfair, Rail Fence, Substitution
│   │   │   │   ├── modern/               # AES (GCM/CBC), RSA (toy math & 2048b JCA), Diffie-Hellman
│   │   │   │   ├── hashing/              # MD5, SHA family, Avalanche Effect analyzer
│   │   │   │   ├── signatures/           # RSA-SHA256, ECDSA, Tamper verification
│   │   │   │   ├── cryptanalysis/        # Frequency analysis, IoC, Caesar brute force with Chi-Square
│   │   │   │   ├── steganography/        # CLAB container, LSB PNG/BMP, MSE/PSNR, difference maps
│   │   │   │   ├── steganalysis/         # Bit-plane visual filter, parity anomaly metrics
│   │   │   │   ├── security/             # SecureRandomProvider, PasswordKdf (PBKDF2)
│   │   │   │   └── storage/              # SQLite DatabaseManager, ExperimentRepository
│   │   │   ├── educational/exercises/    # Coursework challenges & ExerciseManager
│   │   │   └── ui/                       # Modern JavaFX UI components & views
│   │   │       ├── components/           # LabCard, SecurityNoticeBanner, SvgIcons
│   │   │       ├── navigation/           # SidebarView, HeaderBar, StatusBar
│   │   │       ├── dashboard/            # DashboardView
│   │   │       ├── about/                # AboutDialog
│   │   │       └── views/
│   │   │           ├── classical/        # ClassicalLabView
│   │   │           ├── modern/           # ModernLabView
│   │   │           ├── cryptanalysis/    # CryptanalysisLabView
│   │   │           ├── steganography/    # SteganographyLabView
│   │   │           ├── signatures/       # SignaturesAndHashLabView
│   │   │           ├── exercises/        # ExercisesView
│   │   │           ├── history/          # HistoryView
│   │   │           └── learning/         # LearningExplorerView
│   │   └── resources/
│   │       ├── css/dark-lab.css          # Dark cybersecurity laboratory stylesheet
│   │       └── logback.xml               # Structured logging configuration
│   └── test/
│       └── java/com/cryptolab/           # Comprehensive JUnit 5 test suite
```

---

## 🧪 Laboratory Modules & Features

### 1. Classical Cryptography Laboratory
* **Caesar Cipher**: Modular addition $C = (P + k) \pmod{26}$ with interactive shift slider, live alphabet wheel mapping, and step-by-step traces.
* **Atbash Cipher**: Symmetric reflection ($A \leftrightarrow Z$, $B \leftrightarrow Y$) involution.
* **Affine Cipher**: Multiplicative and additive transformation $C = (aP + b) \pmod{26}$ with extended Euclidean modular inverse calculation and coprime validation for $a \in \{1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25\}$.
* **Vigenère Cipher**: Polyalphabetic keyword-driven Caesar shifts with key alignment visualization.
* **Playfair Cipher**: $5 \times 5$ polybius matrix (I/J merged), digram splitting, repeated letter 'X' insertion, and directional matrix shift rules.
* **Rail Fence Cipher**: Transposition zigzag across $N$ rails with path tracing and row reconstruction on decryption.
* **Simple Substitution Cipher**: Arbitrary 26-letter permutation key with one-click random key shuffle generator.

### 2. Modern Cryptography Laboratory
* **AES (Advanced Encryption Standard)**:
  * Modes: **AES-GCM** (Authenticated Encryption with Associated Data, 128-bit tag, 96-bit nonce) and **AES-CBC** (PKCS5Padding).
  * Key Sizes: 128-bit, 192-bit, and 256-bit with random cryptographic key and IV generators.
  * Tamper Detection: Detects single-bit ciphertext modifications and throws authentication failure.
* **RSA Public-Key Cryptosystem**:
  * **Educational Math Mode**: Generates small primes $p, q$, calculates modulus $n = pq$, Euler's totient $\phi(n) = (p-1)(q-1)$, public exponent $e$, and private exponent $d = e^{-1} \pmod \phi$. Traces step-by-step modular exponentiation: $c = m^e \pmod n$, $m = c^d \pmod n$.
  * **Production Mode**: 2048-bit key pair generation with OAEP padding (`RSA/ECB/OAEPWithSHA-256AndMGF1Padding`).
* **Diffie-Hellman Key Exchange**:
  * Full protocol trace: Public prime $p$ & generator $g$, Alice's private $a$ and public $A = g^a \pmod p$, Bob's private $b$ and public $B = g^b \pmod p$, and shared secret derivation $K = g^{ab} \pmod p$.

### 3. Cryptanalysis Laboratory
* **Character Frequency Analysis**:
  * Computes letter counts, percentage distributions, and peak frequencies.
  * Real-time comparative BarChart overlaying actual ciphertext frequencies against standard English distributions.
  * **Index of Coincidence (IoC)**: Diagnostic metric distinguishing monoalphabetic ciphers ($\approx 0.067$) from polyalphabetic or random text ($\approx 0.038$).
* **Automated Caesar Brute-Force Attack**:
  * Simultaneously decrypts across all 25 non-trivial shifts.
  * Calculates Chi-Square ($\chi^2 = \sum \frac{(O_i - E_i)^2}{E_i}$) distance against natural English.
  * Ranks candidates by lowest $\chi^2$ and displays confidence indicators (High / Moderate / Low).

### 4. Steganography & Steganalysis Laboratory
* **Lossless Image Support**: PNG and BMP formats (avoiding JPEG compression artifacts).
* **LSB (Least Significant Bit) Embedding Engine**:
  * Configurable bit depth: 1, 2, 3, or 4 bits per channel.
  * Selectable color channels: Red, Green, Blue (and Alpha).
  * **CLAB Container Protocol**: Binary packaging with magic header `CLAB`, version, payload type, payload length, and CRC-32 integrity checksum.
  * **AES-256-GCM Password Encryption Layer**: Combines PBKDF2 key derivation (65,536 iterations) with AES-GCM before embedding.
* **Fidelity & Difference Visualization**:
  * Side-by-side preview: Cover Image, Stego Image, and Amplified Difference Map (50x amplification).
  * Metrics: Mean Squared Error (MSE), Peak Signal-to-Noise Ratio (PSNR in dB), and Changed Pixel Percentage.
  * Step-by-step bit-level trace table showing original binary vs modified binary for individual pixels.
* **Steganalysis Module**:
  * Bit-Plane Filter: Renders individual bit planes (0=LSB to 7=MSB) for Red, Green, or Blue.
  * Parity Anomaly Indicator: Estimates LSB zero/one parity balance to flag suspicious hidden payloads.

### 5. Hashing & Digital Signatures Laboratory
* **Cryptographic Hash Functions**:
  * MD5, SHA-1, SHA-256, SHA-384, SHA-512 with digest length and hex formatting.
  * Clear security deprecation notices for MD5 and SHA-1.
* **Avalanche Effect Demonstrator**:
  * Flips 1 single bit in the input message.
  * Compares digests and calculates Hamming distance and percentage of flipped bits (ideal target: $\approx 50\%$).
* **Digital Signatures & Tamper Detection**:
  * Asymmetric schemes: RSA (SHA256withRSA, 2048-bit) and ECDSA (SHA256withECDSA, 256-bit).
  * Generates signer key pairs, signs plaintext messages, and verifies authenticity.
  * Interactive Tamper Demo: Modifies message content by 1 character to immediately demonstrate mathematical verification failure.

### 6. Coursework Exercises & Laboratory Reports
* **Practical Student Challenges**:
  * Preloaded challenges covering Caesar decryption, Atbash decipherment, RSA totient math, steganography capacity calculation, and hash properties.
  * Real-time automated submission grader with instant feedback and pedagogical solution walkthroughs.
* **Session Experiment History**:
  * Embedded SQLite storage (`~/.cipheraz/cipheraz_history.db`).
  * Searchable table of experiment timestamps, algorithms, operations, parameters, and timings.
  * **HTML Report Exporter**: Generates formatted, printable lab reports suitable for university submission.

---

## 🛡️ Security & Threat Modeling

| Threat Vector | Mitigation in CipheRaz |
| :--- | :--- |
| **Accidental Secret Logging** | Logger level strictly controlled; passwords, private keys, and plaintexts are explicitly omitted from logs and database schemas. |
| **Insecure Randomness** | Standard `java.util.Random` is strictly prohibited. All cryptographic keys, IVs, nonces, and salts use `SecureRandomProvider` (`SecureRandom.getInstanceStrong()`). |
| **Decompression / Image Bombs** | Image loading validates raster dimensions and checks byte capacity before processing to prevent heap exhaustion. |
| **Path Traversal Attacks** | File chooser dialogs restrict file paths to user-selected targets; relative directory paths are strictly sanitized. |
| **Ciphertext & Stego Tampering** | AES-GCM tags and CLAB container CRC-32/tag checks detect byte modifications before processing. |
| **Network Leakage** | The application runs 100% offline with zero remote telemetry or external API calls. |

---

## 🚀 Build, Run & Package Instructions

### Prerequisites
* Java 21 LTS (or compatible JDK 21+)
* Git

### 1. Compile & Execute Tests
```bash
./mvnw clean test
```
All 28 automated tests will compile and execute.

### 2. Run the Application in Development Mode
```bash
./mvnw javafx:run
```

### 3. Build Executable Standalone Fat JAR
```bash
./mvnw clean package -DskipTests
```
The standalone fat JAR (~23 MB) is generated at:
```bash
target/cryptolab-1.0.0-SNAPSHOT.jar
```
Run directly with:
```bash
java -jar target/cryptolab-1.0.0-SNAPSHOT.jar
```

### 4. Create Native Installers (jpackage)
To build platform-native installers using Java's built-in `jpackage`:
```bash
./package.sh
```
* **macOS**: Generates `.dmg` and `.app` in `dist/`
* **Windows**: Generates `.msi` in `dist/`
* **Linux**: Generates `.deb` in `dist/`

---

## 👨‍💻 Developer & Attribution

* **Application Name**: **CipheRaz** (Educational Cryptography & Steganography Laboratory)
* **Lead Architect & Developer**: **Abderrazak BENAROUS**
* **Attribution Notice**: "Developed by Abderrazak BENAROUS" is displayed in the main status footer, the About dialog, experiment reports, and project metadata.

---

## 📜 License & Academic Usage

CipheRaz is developed for educational and academic use in university cybersecurity laboratories. Instructors and students are welcome to use it for laboratory coursework, demonstrations, and practical security sessions.
