public class Main {
    public static void main(String[] args) throws Exception {
        TriageSystem triage = new TriageSystem();
        triage.admit("Meena", 1);
        triage.admit("Kumar", 1);
        triage.admit("Lata", 2);
        triage.admit("Ravi", 3);
        triage.admit("John", 3);
        triage.admit("Asha", 5);

        StringBuilder out = new StringBuilder();
        for (TriageSystem.Patient patient : triage.treatmentOrder()) {
            if (out.length() > 0) {
                out.append(' ');
            }
            out.append(patient);
        }
        System.out.println(out);
        TriageSystem.main(new String[0]);
    }
}
