package us.lemin.kitpvp.server;

public enum Buffs {

    PROTECTION(40, 125, 250),
    UNBREAKING(45, 125, 250),
    SHARPNESS(10, 45, 135),
    KNOCKBACK(45, 135),
    POWER(10, 45, 135),
    PUNCH(45, 135),
    POISON(100),
    SPEED(100, 250),
    DIAMOND(250),
    STRENGTH(100, 250);


    private int[] pricesByLevel;

    Buffs(int... prices) {
        pricesByLevel = prices;

        /*for (int i = 0; i < pricesByLevel.length; i++) {
            pricesByLevel[i] = i;
        }*/
    }

    public int getPriceByLevel(int level) {
        return level > pricesByLevel.length ? -1 : pricesByLevel[level];
    }
}
