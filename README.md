# Modification-Tolerant Signature Scheme (MTSS)

This project implements a Modification-Tolerant Signature Scheme (MTSS) based on the thesis: **Modification-Tolerant Signature Schemes using Combinatorial Group Testing: Theory, Algorithms, and Implementation**. This scheme enables robust digital signatures that can withstand certain types of modifications, ensuring reliable verification of file integrity and authenticity.

## Project Overview

The MTSS project includes two main components:

- **MicoSign**: A tool for signing files using a chosen cryptographic algorithm.
- **MicoVerify**: A tool for verifying the integrity and authenticity of signed files.

The project relies on **The Bouncy Castle Cryptographic Library** for cryptographic operations, specifically using `bcprov-jdk18on-177.jar`.

## Prerequisites

- **Java Development Kit (JDK)**: Ensure you have JDK installed and properly configured.
- **Bouncy Castle Cryptographic Library**: Download and add `bcprov-jdk18on-177.jar` to your classpath.

## Installation

1. **Clone the Repository**:
    ```bash
    git clone <repository-link>
    ```

2. **Navigate to the project directory**:
    ```bash
    cd <project-directory>
    ```

3. **Compile the Project**: Navigate to the source directory and compile the Java files with the Bouncy Castle library.
    ```bash
    cd src/mtss
    javac -cp bcprov-jdk18on-177.jar:./ *.java
    ```

## Usage

### MicoSign

The **MicoSign** tool signs files with selected cryptographic options. After compilation, you can run MicoSign as follows:

```bash
java -cp bcprov-jdk18on-177.jar:./ mtss.MicoSign -help
 ```

#### MicoSign Options

- `-a <ecdsa|rsa|sphincsplus|falcon|dilithium>`: Choose the CDSS algorithm.
- `-h <sha2256|sha2512|sha3256|sha3512>`: Select the hash algorithm.
- `-d <integer>`: Specify the maximum number of defectives.
- `-c <sperner|sts|rs>`: Select the CFF Construction Method.
  - `sperner`: Use when `d = 1`
  - `sts` or `rs`: Use when `d = 2`
  - `rs`: Use for any other value of `d`
- `-f <list|compact>`: Format of the CFF matrix representation.
- `-g <image> | -t <text>`: Specify the file type to sign.
  - `g`: For image files
  - `t`: For text files
- `-b <integer> | -z <integer>`: Define block size or number of blocks.
- `-s <String>`: Specify a custom extension for signature files.



## Usage

### MicoVerify

The **MicoVerify** tool verifies the authenticity and integrity of signed files. To use MicoVerify, run:

```bash
java -cp bcprov-jdk18on-177.jar:./ mtss.MicoVerify -help
 ```

#### MicoVerify Options
-k <pem file>: Specify the PEM file containing the public key.
-gt <general|specific>: Select the group testing method.
general: Use the general decoding method.
specific: Use the specific decoding method.
-gp <file>,<signature>: List pairs of files and their corresponding signatures.
Each file and its signature should be separated by a comma (e.g., file.txt,signature.txt).
Leave a space between pairs if verifying multiple files.

## License

This project is licensed under the MIT License.


