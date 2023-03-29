package ru.tolboy.deepcopy.testentities;

import java.util.Set;

public class ManOfMen {

    public enum Possibilities {
        KICK(5),
        PUNCH(10);
        public final int power;
        Possibilities(int power) {
            this.power = power;
        }
    }

    private static Integer copyOfSmithsCounter = 0;
    private final Integer age = 42;
    private final int[] zionCodes = {111, 222, 333};
    private final Object[] suspectNames = {"Neo", "Trinity", "Morpheus"};

    private Set<Possibilities> possibilities;
    private String name = "Smith_";
    private ManOfMen leftAgentSmith;
    private ManOfMen rightAgentSmith;

    public ManOfMen() {
        this.name += ++copyOfSmithsCounter;
    }

    public ManOfMen(ManOfMen leftAgentSmith, ManOfMen rightAgentSmith) {
        this.leftAgentSmith = leftAgentSmith;
        this.rightAgentSmith = rightAgentSmith;
    }

    public int getCopySmithsCounter() {
        return copyOfSmithsCounter;
    }
    public ManOfMen getLeft() {
        return leftAgentSmith;
    }
    public ManOfMen getRight() {
        return rightAgentSmith;
    }
    public Integer getAge() {
        return age;
    }

    public Set<Possibilities> getPossibilities() {
        return possibilities;
    }

    public void setPossibilities(Set<Possibilities> possibilities) {
        this.possibilities = possibilities;
    }

    public int[] getZionCodes() {
        return zionCodes;
    }

    public Object[] getSuspectNames() {
        return suspectNames;
    }
}


