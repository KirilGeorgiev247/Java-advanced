package bg.sofia.uni.fmi.mjt.compass.dto.type;

// TODO: check for dinner/lunch
public enum MealType {
    DEFAULT(""),
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH_DINNER("lunch/dinner"),
    SNACK("snack"),
    TEATIME("teatime");

    private final String type;

    MealType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
