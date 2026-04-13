"""
╔════════════════════════════════════════════════╗
║     DIGITAL LOGIC SIMULATOR                    ║
║     Simple Python Version                      ║
╚════════════════════════════════════════════════╝

This program has 3 sections:
  1. LOGIC GATES  — AND, OR, NOT, NAND, NOR, XOR
  2. MULTIPLEXER  — 2:1 MUX and 4:1 MUX
  3. ADDERS       — Half Adder and Full Adder

HOW TO RUN: python logic_gates.py
"""


# ──────────────────────────────────────────────
# STEP 1: Define each gate as a simple function
# ──────────────────────────────────────────────
# Each function takes 1 or 2 inputs (0 or 1)
# and returns the output (0 or 1).

def AND(a, b):
    """Returns 1 only when BOTH inputs are 1"""
    if a == 1 and b == 1:
        return 1
    return 0

def OR(a, b):
    """Returns 1 when AT LEAST ONE input is 1"""
    if a == 1 or b == 1:
        return 1
    return 0

def NOT(a):
    """Flips the input: 0 becomes 1, 1 becomes 0"""
    if a == 0:
        return 1
    return 0

def NAND(a, b):
    """Opposite of AND (NOT + AND combined)"""
    return NOT(AND(a, b))   # first do AND, then flip it

def NOR(a, b):
    """Opposite of OR (NOT + OR combined)"""
    return NOT(OR(a, b))    # first do OR, then flip it

def XOR(a, b):
    """Returns 1 when inputs are DIFFERENT"""
    if a != b:
        return 1
    return 0


# ──────────────────────────────────────────────
# STEP 2: Truth table printer
# ──────────────────────────────────────────────
# Loops through all possible input combinations
# and shows what the gate outputs for each one.

def print_truth_table(gate_name, gate_function):
    """Prints the complete truth table for any gate"""
    print(f"\n--- Truth Table for {gate_name} ---")

    if gate_name == "NOT":
        # NOT has only 1 input, so just 2 rows
        print("  A | OUT")
        print(" ---|----")
        for a in [0, 1]:                      # try input 0 and 1
            result = gate_function(a)          # compute output
            print(f"  {a} |  {result}")
    else:
        # All other gates have 2 inputs, so 4 rows
        print("  A  B | OUT")
        print(" ------|----")
        for a in [0, 1]:                       # try all combos of a and b
            for b in [0, 1]:
                result = gate_function(a, b)   # compute output
                print(f"  {a}  {b} |  {result}")


# ──────────────────────────────────────────────
# STEP 3: Store all gates in a dictionary
# ──────────────────────────────────────────────
# This lets us look up a gate by its name (string)
# instead of writing a big if-else chain.

gates = {
    "AND":  AND,
    "OR":   OR,
    "NOT":  NOT,
    "NAND": NAND,
    "NOR":  NOR,
    "XOR":  XOR,
}


# ══════════════════════════════════════════════
# STEP 4: MULTIPLEXER (MUX) FUNCTIONS
# ══════════════════════════════════════════════
# A MUX is like a switch/selector.
# It has several INPUTS, a SELECT line, and ONE output.
# The SELECT line decides which input gets passed to output.
#
# Real-life analogy:
#   Imagine a TV remote. You have 100 channels (inputs).
#   The channel number you press (select) decides
#   which channel appears on screen (output).

def mux_2to1(i0, i1, sel):
    """
    2:1 MUX — picks between 2 inputs using 1 select line.

    sel = 0  →  output = i0  (first input wins)
    sel = 1  →  output = i1  (second input wins)

    How it works in hardware (using gates we already know):
      output = (NOT(sel) AND i0) OR (sel AND i1)

    When sel=0: NOT(0)=1, so (1 AND i0)=i0, and (0 AND i1)=0 → output=i0
    When sel=1: NOT(1)=0, so (0 AND i0)=0, and (1 AND i1)=i1 → output=i1
    """
    path_0 = AND(NOT(sel), i0)   # this path is active when sel = 0
    path_1 = AND(sel, i1)        # this path is active when sel = 1
    output  = OR(path_0, path_1) # combine — only one path has a value
    return output


def mux_4to1(i0, i1, i2, i3, s1, s0):
    """
    4:1 MUX — picks between 4 inputs using 2 select lines.

    s1=0, s0=0 → output = i0
    s1=0, s0=1 → output = i1
    s1=1, s0=0 → output = i2
    s1=1, s0=1 → output = i3

    Think of s1 and s0 as a 2-digit binary number:
      00 = 0 → i0
      01 = 1 → i1
      10 = 2 → i2
      11 = 3 → i3

    Built from two 2:1 MUXes and one more 2:1 MUX:
      - First MUX picks between i0 and i1 (using s0)
      - Second MUX picks between i2 and i3 (using s0)
      - Third MUX picks between those two results (using s1)
    """
    top    = mux_2to1(i0, i1, s0)    # pick between i0, i1
    bottom = mux_2to1(i2, i3, s0)    # pick between i2, i3
    output = mux_2to1(top, bottom, s1)  # pick between the two winners
    return output


def run_mux():
    """Interactive MUX simulator — asks user for inputs and shows result"""
    print("\n--- MULTIPLEXER ---")
    print("  1. 2:1 MUX (2 inputs, 1 select)")
    print("  2. 4:1 MUX (4 inputs, 2 selects)")

    pick = input("Choose (1 or 2): ").strip()

    try:
        if pick == "1":
            # ── 2:1 MUX ──
            i0  = int(input("  Input I0 (0 or 1): "))
            i1  = int(input("  Input I1 (0 or 1): "))
            sel = int(input("  Select S (0 or 1): "))

            result = mux_2to1(i0, i1, sel)

            # Show step-by-step explanation
            print(f"\n  Step-by-step:")
            print(f"    S = {sel}")
            print(f"    S picks I{sel} = {i0 if sel == 0 else i1}")
            print(f"    Formula: (NOT({sel}) AND {i0}) OR ({sel} AND {i1})")
            print(f"  ✅ Output = {result}")

        elif pick == "2":
            # ── 4:1 MUX ──
            i0 = int(input("  Input I0 (0 or 1): "))
            i1 = int(input("  Input I1 (0 or 1): "))
            i2 = int(input("  Input I2 (0 or 1): "))
            i3 = int(input("  Input I3 (0 or 1): "))
            s1 = int(input("  Select S1 (0 or 1): "))
            s0 = int(input("  Select S0 (0 or 1): "))

            result = mux_4to1(i0, i1, i2, i3, s1, s0)

            # Which input index was selected
            index = s1 * 2 + s0      # convert binary to decimal
            inputs_list = [i0, i1, i2, i3]

            print(f"\n  Step-by-step:")
            print(f"    S1={s1}, S0={s0} → binary {s1}{s0} = index {index}")
            print(f"    Index {index} picks I{index} = {inputs_list[index]}")
            print(f"  ✅ Output = {result}")

        else:
            print("❌ Pick 1 or 2.")

    except ValueError:
        print("❌ Please enter 0 or 1 only.")


# ══════════════════════════════════════════════
# STEP 5: BINARY ADDER FUNCTIONS
# ══════════════════════════════════════════════
# Adders are how computers do math (addition).
# Every time you write "a + b" in code, the CPU
# uses adder circuits to calculate the answer.

def half_adder(a, b):
    """
    HALF ADDER — adds two single bits.

    Returns two values:
      sum   = the "ones" column result
      carry = the "tens" column result (did it overflow?)

    Uses only XOR and AND gates:
      sum   = A XOR B   (are they different? → 1)
      carry = A AND B   (are both 1? → carry over)

    Examples:
      0 + 0 = 00 → sum=0, carry=0
      0 + 1 = 01 → sum=1, carry=0
      1 + 0 = 01 → sum=1, carry=0
      1 + 1 = 10 → sum=0, carry=1  (1+1=2, which is "10" in binary)

    WHY "HALF"?
      It can't accept a carry from a previous addition.
      For that, you need a Full Adder.
    """
    s = XOR(a, b)   # sum bit
    c = AND(a, b)   # carry bit
    return s, c


def full_adder(a, b, carry_in):
    """
    FULL ADDER — adds two bits PLUS a carry from a previous stage.

    This is the building block for adding multi-bit numbers.

    How it works (using two half adders):
      1. First half adder:  add A + B → get partial_sum, partial_carry
      2. Second half adder: add partial_sum + carry_in → get final_sum, carry2
      3. Final carry = partial_carry OR carry2

    Example: a=1, b=1, carry_in=1
      Step 1: 1+1 → partial_sum=0, partial_carry=1
      Step 2: 0+1 → final_sum=1, carry2=0
      Step 3: carry_out = 1 OR 0 = 1
      Answer: sum=1, carry=1 → binary "11" = decimal 3 ✓ (1+1+1=3)
    """
    # First half adder: A + B
    partial_sum, partial_carry = half_adder(a, b)

    # Second half adder: partial_sum + carry_in
    final_sum, carry2 = half_adder(partial_sum, carry_in)

    # Carry out: either the first or second adder generated a carry
    carry_out = OR(partial_carry, carry2)

    return final_sum, carry_out


def run_adder():
    """Interactive Adder simulator — asks user for inputs and shows result"""
    print("\n--- BINARY ADDER ---")
    print("  1. Half Adder (A + B)")
    print("  2. Full Adder (A + B + Carry In)")

    pick = input("Choose (1 or 2): ").strip()

    try:
        if pick == "1":
            # ── HALF ADDER ──
            a = int(input("  Input A (0 or 1): "))
            b = int(input("  Input B (0 or 1): "))

            s, c = half_adder(a, b)

            print(f"\n  Step-by-step:")
            print(f"    Sum   = A XOR B = {a} XOR {b} = {s}")
            print(f"    Carry = A AND B = {a} AND {b} = {c}")
            print(f"  ✅ Result: {c}{s} (decimal {c * 2 + s})")
            print(f"     Meaning: {a} + {b} = {a + b}")

        elif pick == "2":
            # ── FULL ADDER ──
            a   = int(input("  Input A (0 or 1): "))
            b   = int(input("  Input B (0 or 1): "))
            cin = int(input("  Carry In (0 or 1): "))

            s, cout = full_adder(a, b, cin)

            # Show the two-stage process
            ps, pc = half_adder(a, b)       # redo stage 1 for display
            fs, c2 = half_adder(ps, cin)    # redo stage 2 for display

            print(f"\n  Step-by-step:")
            print(f"    Stage 1: Half Adder({a}, {b})")
            print(f"      partial_sum={ps}, partial_carry={pc}")
            print(f"    Stage 2: Half Adder({ps}, {cin})")
            print(f"      final_sum={fs}, carry2={c2}")
            print(f"    Stage 3: carry_out = {pc} OR {c2} = {cout}")
            print(f"  ✅ Result: {cout}{s} (decimal {cout * 2 + s})")
            print(f"     Meaning: {a} + {b} + {cin} = {a + b + cin}")

        else:
            print("❌ Pick 1 or 2.")

    except ValueError:
        print("❌ Please enter 0 or 1 only.")


# ══════════════════════════════════════════════
# STEP 6: Main Menu — choose which tool to use
# ══════════════════════════════════════════════
# Keeps running until the user types "quit".

print("=" * 42)
print("   DIGITAL LOGIC SIMULATOR")
print("   Type 'quit' to exit anytime")
print("=" * 42)

while True:
    # Show the main menu
    print("\n┌──────────────────────────────┐")
    print("│  Choose a tool:              │")
    print("│   1. Logic Gates             │")
    print("│   2. Multiplexer (MUX)       │")
    print("│   3. Binary Adder            │")
    print("└──────────────────────────────┘")

    choice = input("Enter 1, 2, 3 (or 'quit'): ").strip().lower()

    if choice == "quit":
        print("Goodbye!")
        break

    # ── OPTION 1: Logic Gates ──
    elif choice == "1":
        print("\nAvailable gates: AND, OR, NOT, NAND, NOR, XOR")
        gate_name = input("Pick a gate: ").strip().upper()

        if gate_name not in gates:
            print("❌ Unknown gate. Try again.")
            continue

        gate_fn = gates[gate_name]

        try:
            a = int(input("Enter input A (0 or 1): "))

            if gate_name == "NOT":
                result = gate_fn(a)
                print(f"\n✅ NOT({a}) = {result}")
            else:
                b = int(input("Enter input B (0 or 1): "))
                result = gate_fn(a, b)
                print(f"\n✅ {a} {gate_name} {b} = {result}")

        except ValueError:
            print("❌ Please enter 0 or 1 only.")
            continue

        show = input("Show truth table? (y/n): ").strip().lower()
        if show == "y":
            print_truth_table(gate_name, gate_fn)

    # ── OPTION 2: Multiplexer ──
    elif choice == "2":
        run_mux()

    # ── OPTION 3: Adder ──
    elif choice == "3":
        run_adder()

    else:
        print("❌ Enter 1, 2, or 3.")