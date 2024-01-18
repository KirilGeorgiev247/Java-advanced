package bg.sofia.uni.fmi.mjt.compass.api.request.recipetype;

public enum MealType {
    DEFAULT(""),
    BREAKFAST("breakfast"),
    BRUNCH("brunch"),
    LUNCH_DINNER("lunch/dinner"),
    LUNCH("lunch"),
    DINNER("dinner"),
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
