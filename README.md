# Modification-Tolerant Signature Scheme (MTSS)

This project implements a Modification-Tolerant Signature Scheme (MTSS) based on the thesis: **Modification-Tolerant Signature Schemes using Combinatorial Group Testing: Theory, Algorithms, and Implementation**. You can read the full thesis [here](https://ruor.uottawa.ca/items/aa615ffa-bb91-4f99-92ba-abb54501f9e6).
This scheme enables robust digital signatures that can locate up to certain numbers (d) of modifications. 

## Thesis Abstract 

Matrices that are d-cover-free are used in non-adaptive combinatorial group testing (CGT).
They allow for locating up to d defectives within a set of n items by testing them in groups.
In non-adaptive group testing, all tests are determined before any are conducted. A group
test yields a negative result if and only if all items in the group are non-defective. In this
thesis, we study d-cover-free families (CFFs) built from set systems and codes. Examples of
these include Sperner sets, Steiner Triple Systems, and Reed-Solomon codes. Our primary
focus is on decoding algorithms of CGT, which are used to accurately identify all defectives
based on the test results. We introduce a general decoding algorithm that relies solely on
the d-CFF property of the matrix, as well as specialized algorithms designed according to
the specific construction of d-CFFs. Additionally, we conduct experiments to evaluate the
performance of each decoding method and compare the general and specialized algorithms,
particularly in terms of their efficiency as n and d grow.

This study also explores the application of non-adaptive combinatorial group testing
using d-CFF to digital signatures. Digital signatures are mathematical schemes designed to
ensure data integrity and authenticity of digital communications. They verify the authentic-
ity of a signature and ensure that the signed document has not been tampered with. These
schemes include classical methods like RSA-based and ECDSA, as well as post-quantum
approaches such as FALCON and SPHINCS+. However, they lack the ability to identify the
location of changes if a document has been modified. By incorporating d-CFFs into these
digital signatures, we can not only verify the authenticity of the signature, but also locate
up to d modifications, leading to what is known as d-modification tolerant signature schemes
(MTSS).

Our work advances the study of modification-tolerant signature schemes (MTSS) by
practically implementing d-MTSS for various document types. Key achievements include
developing efficient data structures and decoding algorithms for different constuctions of
d-CFFs. We also conduct extensive testing of d-MTSS across multiple scenarios, and our
results demonstrate its viability where document modifications are necessary. This research
paves the way for future enhancements in data security and digital signature methods.

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
- `-k <pem file>`: Specify the PEM file containing the public key.
- `-gt <general|specific>`: Select the group testing method.
  - `general`: Use the general decoding method.
  - `specific`: Use the specific decoding method.
- `-gp <file>,<signature>`: List pairs of files and their corresponding signatures.
  - Each file and its signature should be separated by a comma (e.g., `file.txt,signature.txt`).
  - Leave a space between pairs if verifying multiple files.

## License

This project is licensed under the MIT License.


