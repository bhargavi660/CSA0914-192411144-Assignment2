import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        System.out.println("Task 3: HashSet contract and mutable-key example");

        Set<BrokenStudent> broken = new HashSet<>();
        BrokenStudent s1 = new BrokenStudent("REG-900", "Asha");
        BrokenStudent s2 = new BrokenStudent("REG-900", "Aisha");
        broken.add(s1);
        broken.add(s2);
        System.out.println("Broken students with same regNo in HashSet: " + broken.size());

        Set<BrokenStudent> repeated = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            repeated.add(new BrokenStudent("REG-777", "Student " + i));
        }
        System.out.println("Broken repeated scans of same regNo: " + repeated.size());

        MutableStudent mutable = new MutableStudent("REG-100", "Nila");
        Set<MutableStudent> mutableSet = new HashSet<>();
        mutableSet.add(mutable);
        mutable.setRegNo("REG-999");
        System.out.println("Mutable key after insertion: " + mutableSet.size());

        Set<CorrectStudent> correct = new HashSet<>();
        correct.add(new CorrectStudent("REG-201", "Alpha"));
        correct.add(new CorrectStudent("REG-220", "Beta"));
        correct.add(new CorrectStudent("REG-201", "Alpha Duplicate"));
        System.out.println("Corrected students with immutable regNo: " + correct.size());

        System.out.println("Corrected design matches the assignment because two logical students remain distinct.");
    }

    private static final class BrokenStudent {
        private final String regNo;
        private final String name;

        private BrokenStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) { return true; }
            if (!(obj instanceof BrokenStudent)) { return false; }
            BrokenStudent other = (BrokenStudent) obj;
            return regNo.equals(other.regNo);
        }

        @Override
        public String toString() {
            return regNo + "(" + name + ")";
        }
    }

    private static final class MutableStudent {
        private String regNo;
        private final String name;

        private MutableStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        public void setRegNo(String regNo) {
            this.regNo = regNo;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) { return true; }
            if (!(obj instanceof MutableStudent)) { return false; }
            MutableStudent other = (MutableStudent) obj;
            return regNo.equals(other.regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }
    }

    private static final class CorrectStudent {
        private final String regNo;
        private final String name;

        private CorrectStudent(String regNo, String name) {
            this.regNo = regNo;
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) { return true; }
            if (!(obj instanceof CorrectStudent)) { return false; }
            CorrectStudent other = (CorrectStudent) obj;
            return regNo.equals(other.regNo);
        }

        @Override
        public int hashCode() {
            return regNo.hashCode();
        }
    }
}
