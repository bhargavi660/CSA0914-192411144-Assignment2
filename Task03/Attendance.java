import java.util.HashSet;
import java.util.Set;

public class Attendance {
    public static void main(String[] args) {
        Set<CorrectStudent> students = new HashSet<>();
        students.add(new CorrectStudent("REG-101", "Aarav"));
        students.add(new CorrectStudent("REG-204", "Bhavya"));
        students.add(new CorrectStudent("REG-101", "Aarav Duplicate"));
        System.out.println("Unique students: " + students.size());
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

        @Override
        public String toString() {
            return regNo + "(" + name + ")";
        }
    }
}
