package database;

/**
 * Enum for property city codes
 * @author Alex Costello
 */
public enum CityCode {
    ABQ("Albuquerque"), SAF("Santa Fe"), ROW("Roswell"); // Incomplete list
    private String fullName;
    private final static Integer minLength = 3;
    private final static Integer maxLength = 3;
    
    CityCode(String name) {
        fullName = name;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public static Integer getMinLength() {
        return minLength;
    }
    
    public static Integer getMaxLength() {
        return maxLength;
    }
    
    /**
     * For double-checking city code
     * @param code String version of city code
     */
    public static void validateCode(String code) {
        String valid = "";
        for (CityCode c : CityCode.values()) {
            if (code.equals(c.toString())) {
                return;
            }
            valid += c.toString();
            valid += ", ";
        }
        valid = valid.substring(0, valid.length() - 3);
        
        throw new IllegalArgumentException(String.format("Invalid city code '%s'", code, valid));
    }
}
