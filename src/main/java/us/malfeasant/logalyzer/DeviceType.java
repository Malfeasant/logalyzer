package us.malfeasant.logalyzer;

/**
 * All possible device types by driver, including logic to recognize them
 */
public enum DeviceType {
    ARCA2600("Arca2600"),
    ARCA("Arca7000", "Arca8000"),
    COINSIM("CFMCoin"),
    DELARUECOIN("DeLaRueCoin"),
    ECD("DieBoldECD", "DieBold1053"),
    ECR("CM18Solo", "CM18EVO", "ECR", "CM18"),
    FUJI("Fujitsu"),
    PD600("GloryPD600"),
    PD61("GloryPD600S", "GloryPD61"),
    RBG("GloryRbg100"),
    RBU("GloryRbu11"),
    GUNNEBO("GunneboSafeCash"),
    HYOSUNG("HyosungMS400A", "HyosungMS500", "HyosungMS500S"),
    LTA("LgLta350", "BranchServLta450"),
    MDDM("DeLaRueTCD"),
    NCR("NCRCashRecycler", "CimaAst7016"),
    SIM("CFMRTA", "CFMCDR", "CFMDispenser"),
    TQ("TelequipCoin", "CX25Coin"),
    VERT6G("Vertera6G"),
    VERT("DeLaRueTS", "DeLaRueV", "Vertera6GS"),
    CIMA("CimaAst9000"),

    UNKNOWN();

    private final String[] names;

    DeviceType(String... names) {
        this.names = names;
    }

    /**
     * Given a string, returns the appropriate enum constant.
     * Naive implementation- might be better to make a map of Strings to enum constants-
     * but this is probably good enough.
     * @param id text representation of a device type
     * @return an enum constant, or UNKNOWN if not found.
     */
    public static DeviceType from(String id) {
        for (var t : values()) {
            for (var s : t.names) {
                if (id.equals(s)) return t;
            }
        }
        return UNKNOWN;
    }
}
