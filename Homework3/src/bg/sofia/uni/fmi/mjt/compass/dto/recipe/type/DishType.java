package bg.sofia.uni.fmi.mjt.compass.dto.recipe.type;

public enum DishType {
    DEFAULT(""),
    ALCOHOL_COCKTAIL("alcohol cocktail"),
    BISCUITS_COOKIES("biscuits and cookies"),
    BREAD("bread"),
    CEREALS("cereals"),
    CONDIMENTS_SAUCES("condiments and sauces"),
    DESSERTS("desserts"),
    DRINKS("drinks"),
    EGG("egg"),
    ICE_CREAM_CUSTARD("ice cream and custard"),
    MAIN_COURSE("main course"),
    PANCAKE("pancake"),
    PASTA("pasta"),
    PASTRY("pastry"),
    PIES_TARTS("pies and tarts"),
    PIZZA("pizza"),
    PREPS("preps"),
    PRESERVE("preserve"),
    SALAD("salad"),
    SANDWICHES("sandwiches"),
    SEAFOOD("seafood"),
    SIDE_DISH("side dish"),
    SOUP("soup"),
    SPECIAL_OCCASIONS("special occasions"),
    STARTER("starter"),
    SWEETS("sweets");
    private final String type;

    DishType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
