package org.perturbed.nesjs.core.client;

/**
 * Created by matt on 15/03/2014.
 *
 * 7  6  5  4  3  2  1  0
 * N  V  U  B  D  I  Z  C
 */
public class ProcessorStatus {

  private int carry;
  private int notZero;
  private int interruptDisable;
  private int previousInterruptDisable;
  private int decimal;
  private int overflow;
  private int negative;

  public ProcessorStatus() {
    this.clear();
  }

  public void clear() {
    carry = 0;
    notZero = 1;
    interruptDisable = 0;
    previousInterruptDisable = 0;
    decimal = 0;
    overflow = 0;
    negative = 0;
  }

  public int getCarry() {
    return carry;
  }

  public void setCarry(final int carry) {
    this.carry = carry;
  }

  public void setNotZero(final int notZero) {
    this.notZero = notZero;
  }

  public boolean isInterruptDisable() {
    return this.previousInterruptDisable == 0;
  }

  public void setInterruptDisableWithDelay(final int interruptDisable) {
    this.previousInterruptDisable = this.interruptDisable;
    this.interruptDisable = interruptDisable;
  }

  public void setInterruptDisableImmediately(final int interruptDisable) {
    this.previousInterruptDisable = interruptDisable;
    this.interruptDisable = interruptDisable;
  }

  public void clockInterruptDelay() {
    this.previousInterruptDisable = this.interruptDisable;
  }

  public void setDecimal(final int decimal) {
    this.decimal = decimal;
  }

  public void setOverflow(final int overflow) {
    this.overflow = overflow;
  }

  public void setNegative(final int negative) {
    this.negative = negative;
  }

  public boolean isZeroFlagSet() {
    return notZero == 0;
  }

  public boolean isCarryFlagSet() {
    return carry > 0;
  }

  public boolean isNegativeFlagSet() {
    return negative > 0;
  }

  public boolean isOverflowFlagSet() {
    return overflow > 0;
  }

  public void setNZFlag(int value) {
    negative = (value >> 7) & 1;
    notZero = value & 0xFF;
  }

  public int getRegisterValue() {
    return getRegisterValue(0);
  }

  public int getRegisterValue(int brkValue) {
    assert brkValue == 0 || brkValue == 1;

    int zero = isZeroFlagSet() ? 1 : 0;
    return carry | (zero << 1) | (interruptDisable << 2) | (decimal << 3) | (brkValue << 4) |
        (1 << 5) | (overflow << 6) | (negative << 7);
  }
}
