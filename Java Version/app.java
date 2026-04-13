/*
 * ╔════════════════════════════════════════════════╗
 * ║     DIGITAL LOGIC SIMULATOR                    ║
 * ║     Java Version (Beginner Friendly)           ║
 * ╚════════════════════════════════════════════════╝
 *
 * This program has 3 sections:
 *   1. LOGIC GATES  — AND, OR, NOT, NAND, NOR, XOR
 *   2. MULTIPLEXER  — 2:1 MUX and 4:1 MUX
 *   3. ADDERS       — Half Adder and Full Adder
 *
 * HOW TO RUN:
 *   javac app.java
 *   java app
 */

import java.util.Scanner;   // Scanner lets us read user input from the keyboard


public class app {

    // ══════════════════════════════════════════════
    // SECTION 1: LOGIC GATE FUNCTIONS
    // ══════════════════════════════════════════════
    // Each method takes 1 or 2 inputs (0 or 1)
    // and returns the output (0 or 1).
    // "static" means we can call them without creating an object.

    /** AND Gate: Returns 1 only when BOTH inputs are 1 */
    static int AND(int a, int b) {
        if (a == 1 && b == 1) {
            return 1;
        }
        return 0;
    }

    /** OR Gate: Returns 1 when AT LEAST ONE input is 1 */
    static int OR(int a, int b) {
        if (a == 1 || b == 1) {
            return 1;
        }
        return 0;
    }

    /** NOT Gate: Flips the bit — 0 becomes 1, 1 becomes 0 */
    static int NOT(int a) {
        if (a == 0) {
            return 1;
        }
        return 0;
    }

    /** NAND Gate: Opposite of AND (first AND, then flip) */
    static int NAND(int a, int b) {
        return NOT(AND(a, b));
    }

    /** NOR Gate: Opposite of OR (first OR, then flip) */
    static int NOR(int a, int b) {
        return NOT(OR(a, b));
    }

    /** XOR Gate: Returns 1 when inputs are DIFFERENT */
    static int XOR(int a, int b) {
        if (a != b) {
            return 1;
        }
        return 0;
    }


    // ══════════════════════════════════════════════
    // SECTION 2: TRUTH TABLE PRINTER
    // ══════════════════════════════════════════════
    // Loops through every possible input combination
    // and shows the gate output for each one.

    static void printTruthTable(String gateName) {
        System.out.println("\n--- Truth Table for " + gateName + " ---");

        if (gateName.equals("NOT")) {
            // NOT has 1 input → 2 rows
            System.out.println("  A | OUT");
            System.out.println(" ---|----");
            for (int a = 0; a <= 1; a++) {
                int result = NOT(a);
                System.out.println("  " + a + " |  " + result);
            }
        } else {
            // All other gates have 2 inputs → 4 rows
            System.out.println("  A  B | OUT");
            System.out.println(" ------|----");
            for (int a = 0; a <= 1; a++) {
                for (int b = 0; b <= 1; b++) {
                    int result = computeGate(gateName, a, b);
                    System.out.println("  " + a + "  " + b + " |  " + result);
                }
            }
        }
    }

    /**
     * Helper: calls the right gate function based on the name string.
     * This replaces the Python dictionary — Java doesn't have that shortcut,
     * so we use a simple switch statement instead.
     */
    static int computeGate(String gateName, int a, int b) {
        switch (gateName) {
            case "AND":  return AND(a, b);
            case "OR":   return OR(a, b);
            case "NAND": return NAND(a, b);
            case "NOR":  return NOR(a, b);
            case "XOR":  return XOR(a, b);
            case "NOT":  return NOT(a);      // b is ignored for NOT
            default:     return -1;          // -1 means unknown gate
        }
    }


    // ══════════════════════════════════════════════
    // SECTION 3: MULTIPLEXER (MUX) FUNCTIONS
    // ══════════════════════════════════════════════
    // A MUX is a "data selector" — it picks ONE input
    // and sends it to the output. The SELECT line
    // decides which input gets picked.
    //
    // Real-life analogy:
    //   TV remote → channel number (select) decides
    //   which channel (input) appears on screen (output).

    /**
     * 2:1 MUX — picks between 2 inputs using 1 select line.
     *
     * sel = 0  →  output = i0
     * sel = 1  →  output = i1
     *
     * Hardware formula (built from AND, OR, NOT gates):
     *   output = (NOT(sel) AND i0) OR (sel AND i1)
     */
    static int mux2to1(int i0, int i1, int sel) {
        int path0 = AND(NOT(sel), i0);   // active when sel = 0
        int path1 = AND(sel, i1);        // active when sel = 1
        return OR(path0, path1);         // combine both paths
    }

    /**
     * 4:1 MUX — picks between 4 inputs using 2 select lines.
     *
     * s1=0, s0=0 → i0       s1=1, s0=0 → i2
     * s1=0, s0=1 → i1       s1=1, s0=1 → i3
     *
     * Built from three 2:1 MUXes:
     *   top    = mux2to1(i0, i1, s0)      — pick between i0, i1
     *   bottom = mux2to1(i2, i3, s0)      — pick between i2, i3
     *   output = mux2to1(top, bottom, s1)  — pick the winner
     */
    static int mux4to1(int i0, int i1, int i2, int i3, int s1, int s0) {
        int top    = mux2to1(i0, i1, s0);
        int bottom = mux2to1(i2, i3, s0);
        return mux2to1(top, bottom, s1);
    }


    // ══════════════════════════════════════════════
    // SECTION 4: BINARY ADDER FUNCTIONS
    // ══════════════════════════════════════════════
    // Adders are how computers do addition.
    // Every "a + b" in your code uses adder circuits
    // inside the CPU.
    //
    // We return results as int arrays: [sum, carry]

    /**
     * HALF ADDER — adds two single bits.
     *
     * Returns: [sum, carry]
     *   sum   = A XOR B   (are they different?)
     *   carry = A AND B   (did it overflow?)
     *
     * Examples:
     *   0+0 = [0,0]    1+0 = [1,0]
     *   0+1 = [1,0]    1+1 = [0,1]  ← 1+1=2, binary "10"
     *
     * WHY "HALF"?
     *   It can't handle a carry from a previous addition.
     */
    static int[] halfAdder(int a, int b) {
        int sum   = XOR(a, b);   // sum bit
        int carry = AND(a, b);   // carry bit
        return new int[]{sum, carry};
        // new int[]{sum, carry} creates a small array to return both values
    }

    /**
     * FULL ADDER — adds two bits PLUS a carry-in.
     *
     * Returns: [sum, carryOut]
     *
     * Built from two half adders:
     *   Stage 1: halfAdder(A, B)           → partial_sum, partial_carry
     *   Stage 2: halfAdder(partial_sum, Cin) → final_sum, carry2
     *   carryOut = partial_carry OR carry2
     *
     * Example: a=1, b=1, cin=1
     *   Stage 1: 1+1 → sum=0, carry=1
     *   Stage 2: 0+1 → sum=1, carry=0
     *   carryOut: 1 OR 0 = 1
     *   Answer: [1, 1] → binary "11" = decimal 3  ✓
     */
    static int[] fullAdder(int a, int b, int carryIn) {
        // Stage 1: add A + B
        int[] stage1 = halfAdder(a, b);
        int partialSum   = stage1[0];   // index 0 = sum
        int partialCarry = stage1[1];   // index 1 = carry

        // Stage 2: add partialSum + carryIn
        int[] stage2 = halfAdder(partialSum, carryIn);
        int finalSum = stage2[0];
        int carry2   = stage2[1];

        // Carry out: either stage produced a carry
        int carryOut = OR(partialCarry, carry2);

        return new int[]{finalSum, carryOut};
    }


    // ══════════════════════════════════════════════
    // SECTION 5: INTERACTIVE MENU RUNNERS
    // ══════════════════════════════════════════════
    // Each method handles user input for one section.

    /** Runs the Logic Gates interactive mode */
    static void runGates(Scanner sc) {
        System.out.println("\nAvailable gates: AND, OR, NOT, NAND, NOR, XOR");
        System.out.print("Pick a gate: ");
        String gate = sc.nextLine().trim().toUpperCase();

        // Check if gate name is valid
        String[] validGates = {"AND", "OR", "NOT", "NAND", "NOR", "XOR"};
        boolean found = false;
        for (String g : validGates) {
            if (g.equals(gate)) {
                found = true;
                break;       // stop searching once found
            }
        }
        if (!found) {
            System.out.println("Unknown gate. Try again.");
            return;          // go back to main menu
        }

        // Get inputs
        System.out.print("Enter input A (0 or 1): ");
        int a = sc.nextInt();
        sc.nextLine();       // consume the leftover newline character

        if (gate.equals("NOT")) {
            // NOT only needs 1 input
            int result = NOT(a);
            System.out.println("\n  NOT(" + a + ") = " + result);
        } else {
            // All other gates need 2 inputs
            System.out.print("Enter input B (0 or 1): ");
            int b = sc.nextInt();
            sc.nextLine();

            int result = computeGate(gate, a, b);
            System.out.println("\n  " + a + " " + gate + " " + b + " = " + result);
        }

        // Offer truth table
        System.out.print("Show truth table? (y/n): ");
        String show = sc.nextLine().trim().toLowerCase();
        if (show.equals("y")) {
            printTruthTable(gate);
        }
    }

    /** Runs the Multiplexer interactive mode */
    static void runMux(Scanner sc) {
        System.out.println("\n--- MULTIPLEXER ---");
        System.out.println("  1. 2:1 MUX (2 inputs, 1 select)");
        System.out.println("  2. 4:1 MUX (4 inputs, 2 selects)");
        System.out.print("Choose (1 or 2): ");
        String pick = sc.nextLine().trim();

        if (pick.equals("1")) {
            // ── 2:1 MUX ──
            System.out.print("  Input I0 (0 or 1): ");
            int i0 = sc.nextInt();
            System.out.print("  Input I1 (0 or 1): ");
            int i1 = sc.nextInt();
            System.out.print("  Select S (0 or 1): ");
            int sel = sc.nextInt();
            sc.nextLine();

            int result = mux2to1(i0, i1, sel);

            // Step-by-step explanation
            System.out.println("\n  Step-by-step:");
            System.out.println("    S = " + sel);
            System.out.println("    S picks I" + sel + " = " + (sel == 0 ? i0 : i1));
            System.out.println("    Formula: (NOT(" + sel + ") AND " + i0 + ") OR (" + sel + " AND " + i1 + ")");
            System.out.println("  Output = " + result);

        } else if (pick.equals("2")) {
            // ── 4:1 MUX ──
            System.out.print("  Input I0 (0 or 1): ");
            int i0 = sc.nextInt();
            System.out.print("  Input I1 (0 or 1): ");
            int i1 = sc.nextInt();
            System.out.print("  Input I2 (0 or 1): ");
            int i2 = sc.nextInt();
            System.out.print("  Input I3 (0 or 1): ");
            int i3 = sc.nextInt();
            System.out.print("  Select S1 (0 or 1): ");
            int s1 = sc.nextInt();
            System.out.print("  Select S0 (0 or 1): ");
            int s0 = sc.nextInt();
            sc.nextLine();

            int result = mux4to1(i0, i1, i2, i3, s1, s0);
            int index = s1 * 2 + s0;   // binary to decimal
            int[] inputs = {i0, i1, i2, i3};

            System.out.println("\n  Step-by-step:");
            System.out.println("    S1=" + s1 + ", S0=" + s0 + " -> binary " + s1 + "" + s0 + " = index " + index);
            System.out.println("    Index " + index + " picks I" + index + " = " + inputs[index]);
            System.out.println("  Output = " + result);

        } else {
            System.out.println("  Pick 1 or 2.");
        }
    }

    /** Runs the Binary Adder interactive mode */
    static void runAdder(Scanner sc) {
        System.out.println("\n--- BINARY ADDER ---");
        System.out.println("  1. Half Adder (A + B)");
        System.out.println("  2. Full Adder (A + B + Carry In)");
        System.out.print("Choose (1 or 2): ");
        String pick = sc.nextLine().trim();

        if (pick.equals("1")) {
            // ── HALF ADDER ──
            System.out.print("  Input A (0 or 1): ");
            int a = sc.nextInt();
            System.out.print("  Input B (0 or 1): ");
            int b = sc.nextInt();
            sc.nextLine();

            int[] result = halfAdder(a, b);
            int sum   = result[0];
            int carry = result[1];

            System.out.println("\n  Step-by-step:");
            System.out.println("    Sum   = A XOR B = " + a + " XOR " + b + " = " + sum);
            System.out.println("    Carry = A AND B = " + a + " AND " + b + " = " + carry);
            System.out.println("  Result: " + carry + "" + sum + " (decimal " + (carry * 2 + sum) + ")");
            System.out.println("  Meaning: " + a + " + " + b + " = " + (a + b));

        } else if (pick.equals("2")) {
            // ── FULL ADDER ──
            System.out.print("  Input A (0 or 1): ");
            int a = sc.nextInt();
            System.out.print("  Input B (0 or 1): ");
            int b = sc.nextInt();
            System.out.print("  Carry In (0 or 1): ");
            int cin = sc.nextInt();
            sc.nextLine();

            int[] result = fullAdder(a, b, cin);
            int sum  = result[0];
            int cout = result[1];

            // Redo stages for display
            int[] s1 = halfAdder(a, b);
            int[] s2 = halfAdder(s1[0], cin);

            System.out.println("\n  Step-by-step:");
            System.out.println("    Stage 1: HalfAdder(" + a + ", " + b + ")");
            System.out.println("      partial_sum=" + s1[0] + ", partial_carry=" + s1[1]);
            System.out.println("    Stage 2: HalfAdder(" + s1[0] + ", " + cin + ")");
            System.out.println("      final_sum=" + s2[0] + ", carry2=" + s2[1]);
            System.out.println("    Stage 3: carry_out = " + s1[1] + " OR " + s2[1] + " = " + cout);
            System.out.println("  Result: " + cout + "" + sum + " (decimal " + (cout * 2 + sum) + ")");
            System.out.println("  Meaning: " + a + " + " + b + " + " + cin + " = " + (a + b + cin));

        } else {
            System.out.println("  Pick 1 or 2.");
        }
    }


    // ══════════════════════════════════════════════
    // SECTION 6: MAIN METHOD — entry point
    // ══════════════════════════════════════════════
    // Shows the menu and routes to the right section.

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);   // create one Scanner for the whole program

        System.out.println("==========================================");
        System.out.println("   DIGITAL LOGIC SIMULATOR (Java)");
        System.out.println("   Type 'quit' to exit anytime");
        System.out.println("==========================================");

        // Main loop — keeps running until user types "quit"
        while (true) {
            System.out.println("\n+------------------------------+");
            System.out.println("|  Choose a tool:              |");
            System.out.println("|   1. Logic Gates             |");
            System.out.println("|   2. Multiplexer (MUX)       |");
            System.out.println("|   3. Binary Adder            |");
            System.out.println("+------------------------------+");
            System.out.print("Enter 1, 2, 3 (or 'quit'): ");

            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("quit")) {
                System.out.println("Goodbye!");
                break;    // exit the while loop → program ends
            }

            switch (choice) {
                case "1":
                    runGates(sc);    // go to Logic Gates section
                    break;
                case "2":
                    runMux(sc);      // go to Multiplexer section
                    break;
                case "3":
                    runAdder(sc);    // go to Binary Adder section
                    break;
                default:
                    System.out.println("Enter 1, 2, or 3.");
            }
        }

        sc.close();   // close the Scanner when done (good practice)
    }
}