# Upgrade Plan: biodataouh (20260526152306)

- **Generated**: May 26, 2026, 15:23:06
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

> **Note**: This project does not have version control (Git) initialized. Changes will remain in the working directory without commits during this upgrade.

## Available Tools

**JDKs**
- JDK 21.0.11: C:\Program Files\Java\jdk-21.0.11\bin (available for reference)
- JDK 25.0.2: C:\Program Files\Java\jdk-25.0.2\bin (target JDK, used by step 3 onwards)
- JDK 17: not available (baseline will be skipped)

**Build Tools**
- Maven: No Maven installation found (will use system `mvn` command if available, or Maven Central wrapper)

## Guidelines

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20260526152306
- Run tests before and after the upgrade: true

## Upgrade Goals

- **Target Java Version**: Java 25 (LTS)
- **Current Java Version**: Java 17

## Technology Stack

| Technology/Dependency | Current | Min Compatible | Why Incompatible |
|---|---|---|---|
| Java | 17 | 25 | User requested |
| MySQL Connector/J | 9.0.0 | 9.0.0+ | Compatible with Java 25; no upgrade needed |
| JavaFX Controls | 21 | 23 | JavaFX 21 may have compatibility issues with Java 25; upgrade recommended to 23 or later |
| maven.compiler.source | 17 | 25 | Must match target Java version |
| maven.compiler.target | 17 | 25 | Must match target Java version |

## Derived Upgrades

- **JavaFX Controls: 21 → 23**
  - **Justification**: JavaFX 21 was released with Java 21 support. Java 25 is a newer LTS release (3 versions ahead). Upgrading to JavaFX 23 ensures compatibility with Java 25's module system and runtime behavior. JavaFX 23 includes fixes for newer Java versions.

## Impact Analysis

### Subsection: Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|---|---|---|---|---|---|
| pom.xml | maven.compiler.source | 17 | upgrade | 25 | User requested target Java version |
| pom.xml | maven.compiler.target | 17 | upgrade | 25 | User requested target Java version |
| pom.xml | javafx-controls | 21 | upgrade | 23 | Ensure compatibility with Java 25 |
| pom.xml | javafx-fxml (implicit) | 21 | upgrade | 23 | Ensure consistency with javafx-controls |

### Subsection: Source Code Changes

No source code changes required. The application uses standard Java APIs and the module system which are forward-compatible with Java 25. The existing `module-info.java` is compatible with Java 25.

### Subsection: Configuration Changes

No configuration changes required. The application uses default JavaFX and MySQL configurations that are compatible with Java 25.

### Subsection: CI/CD Changes

No CI/CD files detected in the project (no Dockerfile, workflows, or pipeline configs). No changes needed for this section.

### Subsection: Risks & Warnings

- **JavaFX Module System**: JavaFX requires proper module system configuration. The existing `module-info.java` already includes required modules (`javafx.controls`, `javafx.fxml`) and proper `opens` declarations. No additional changes needed.
- **MySQL Connector/J 9.0.0**: Already compatible with Java 25. No compatibility risks.
- **No Test Code Detected**: The `src/test/java` directory exists but appears empty. If tests are added later, ensure they are compatible with Java 25.

## Upgrade Steps

- **Step 1: Setup Environment**
  - **Rationale**: Verify that Java 25 (target JDK) is available and Maven is accessible for building and testing.
  - **Changes to Make**: None (verification only)
  - **Verification**: List available JDKs to confirm Java 25.0.2 is accessible; verify Maven can be invoked.
    - Command: `java -version` (target JDK should show 25.x)
    - Expected Result: Java 25.0.2 successfully located and accessible

- **Step 2: Setup Baseline**
  - **Rationale**: Establish baseline compilation and test results with current Java 17 to measure impact of the upgrade.
  - **Status**: ⏭️ SKIPPED - Java 17 is not available on this system; baseline will be skipped.
  - **Changes to Make**: None
  - **Verification**: Not applicable

- **Step 3: Upgrade Java Version to 25 and JavaFX to 23**
  - **Rationale**: Update the Maven compiler properties to target Java 25, and upgrade JavaFX to version 23 for compatibility with Java 25. These changes work together to align the entire build configuration with the target LTS version.
  - **Changes to Make**:
    - In `pom.xml`: Change `maven.compiler.source` from 17 to 25
    - In `pom.xml`: Change `maven.compiler.target` from 17 to 25
    - In `pom.xml`: Upgrade `javafx-controls` from 21 to 23
    - Add `javafx-fxml` explicitly with version 23 to ensure consistency
  - **Verification**: 
    - Command: `mvn clean compile -q`
    - JDK: Java 25.0.2
    - Expected Result: Compilation successful with no errors; module system resolution successful

- **Step 4: Verify Module System Compatibility**
  - **Rationale**: Ensure the module system configuration in `module-info.java` is fully compatible with Java 25's stricter module encapsulation rules.
  - **Changes to Make**: None (the existing module-info.java is compatible)
  - **Verification**:
    - Command: `mvn clean test-compile -q`
    - JDK: Java 25.0.2
    - Expected Result: Compilation (including test compilation) successful; no module-related errors

- **Step 5: Final Validation**
  - **Rationale**: Run the complete test suite with Java 25 to ensure all functionality works correctly with the upgraded runtime. Validate that the application compiles, runs, and passes all tests.
  - **Changes to Make**: None
  - **Verification**:
    - Compilation: `mvn clean compile -q` → SUCCESS
    - Test Compilation: `mvn clean test-compile -q` → SUCCESS
    - Test Execution: `mvn clean test -q` → 100% PASS RATE (or ≥ baseline if baseline exists)
    - Expected Result: All tests passing with Java 25.0.2; no runtime errors; no deprecation warnings

---

## Summary

This upgrade plan transitions the biodataouh project from **Java 17 to Java 25** with a corresponding JavaFX upgrade (21 → 23) to ensure compatibility. The project uses the module system, which is fully forward-compatible with Java 25. No source code changes are required.

The upgrade consists of:
1. Configuration changes only (pom.xml properties and dependencies)
2. Verification of module system compatibility
3. Complete test validation with Java 25

**Estimated Complexity**: Low - configuration-only changes with no breaking API changes required.
