package com.github.pwittchen.reactivenetwork.library;

public enum WifiSignalLevel {
  NO_SIGNAL(0, "no signal"),
  VERY_POOR(1, "very poor"),
  POOR(2, "poor"),
  MEDIUM(3, "medium"),
  GOOD(4, "good"),
  VERY_GOOD(5, "very good");

  public final int level;
  public final String description;

  WifiSignalLevel(final int level, final String description) {
    this.level = level;
    this.description = description;
  }

  public static int getMaxLevel() {
    return VERY_GOOD.level;
  }

  public static WifiSignalLevel fromLevel(final int level) {
    switch (level) {
      case 0:
        return NO_SIGNAL;
      case 1:
        return VERY_POOR;
      case 2:
        return POOR;
      case 3:
        return MEDIUM;
      case 4:
        return GOOD;
      case 5:
        return VERY_GOOD;
      default:
        return NO_SIGNAL;
    }
  }

  @Override public String toString() {
    return "WifiSignalLevel{" + "level=" + level + ", description='" + description + '\'' + '}';
  }
}
