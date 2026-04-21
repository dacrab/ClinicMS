# Checklist Υποβολής Εργασίας
## CN5004 - Προχωρημένος Προγραμματισμός

---

## Πριν την Υποβολή

### 1. Έλεγχος Κώδικα
- [ ] Το project κάνει compile χωρίς σφάλματα (`mvn clean compile`)
- [ ] Το project εκτελείται χωρίς σφάλματα (`mvn javafx:run`)
- [ ] Όλες οι λειτουργίες δουλεύουν σωστά
- [ ] Υπάρχουν Javadoc comments σε όλες τις κλάσεις
- [ ] Υπάρχουν inline comments για πολύπλοκη λογική
- [ ] Ο κώδικας είναι μορφοποιημένος σωστά
- [ ] Υπάρχουν try-catch blocks για error handling
- [ ] Υπάρχει validation σε όλα τα πεδία

### 2. Έλεγχος Λειτουργικότητας

#### Dashboard
- [ ] Εμφανίζει συνολικούς αριθμούς (ιατροί, ασθενείς, ραντεβού)
- [ ] Εμφανίζει κατανομή ραντεβού ανά κατάσταση
- [ ] Εμφανίζει Top 5 ιατρούς
- [ ] Εμφανίζει πρόσφατα ραντεβού

#### Διαχείριση Ιατρών
- [ ] Προσθήκη νέου ιατρού
- [ ] Ενημέρωση στοιχείων ιατρού
- [ ] Διαγραφή ιατρού (με έλεγχο ενεργών ραντεβού)
- [ ] Αναζήτηση ιατρού σε πραγματικό χρόνο

#### Διαχείριση Ασθενών
- [ ] Εγγραφή νέου ασθενή
- [ ] Ενημέρωση στοιχείων ασθενή
- [ ] Διαγραφή ασθενή (με έλεγχο ενεργών ραντεβού)
- [ ] Αναζήτηση ασθενή σε πραγματικό χρόνο

#### Διαχείριση Ραντεβού
- [ ] Προγραμματισμός νέου ραντεβού
- [ ] Αποτροπή διπλοκράτησης (εμφανίζει σφάλμα)
- [ ] Ενημέρωση ραντεβού
- [ ] Ολοκλήρωση ραντεβού
- [ ] Ακύρωση ραντεβού
- [ ] Φιλτράρισμα ανά κατάσταση

#### Αναφορές
- [ ] Φιλτράρισμα με κατάσταση
- [ ] Φιλτράρισμα με όνομα ιατρού
- [ ] Φιλτράρισμα με όνομα ασθενή
- [ ] Εξαγωγή σε αρχείο `data/report.txt`

### 3. Έλεγχος File I/O
- [ ] Τα δεδομένα αποθηκεύονται σε CSV αρχεία
- [ ] Τα δεδομένα φορτώνονται σωστά κατά την εκκίνηση
- [ ] Τα κόμματα μέσα στα πεδία χειρίζονται σωστά (escape)
- [ ] Ο φάκελος `data/` δημιουργείται αυτόματα

### 4. Έλεγχος OOP
- [ ] Encapsulation: Όλα τα πεδία private με getters/setters
- [ ] Inheritance: `Specialist extends Doctor`
- [ ] Polymorphism: `List<Doctor>` με `Specialist` objects
- [ ] Singleton: `DataStore.getInstance()`
- [ ] Enum: `Appointment.Status`

### 5. Έλεγχος Validation
- [ ] Ημερομηνίες: `ηη/ΜΜ/εεεε`
- [ ] Ώρες: `ΩΩ:ΛΛ`
- [ ] Email: regex pattern
- [ ] Τηλέφωνο: regex pattern
- [ ] Υποχρεωτικά πεδία

### 6. Έλεγχος UI
- [ ] Όλα τα FXML αρχεία υπάρχουν
- [ ] Το CSS αρχείο υπάρχει και εφαρμόζεται
- [ ] Χρωματική κωδικοποίηση καταστάσεων
- [ ] Αναζήτηση σε πραγματικό χρόνο
- [ ] Φιλτράρισμα με πολλαπλά κριτήρια

---

## Έλεγχος Αρχείων

### Κύρια Αρχεία
- [ ] `README.md` - Πλήρης τεκμηρίωση
- [ ] `ΑΝΑΦΟΡΑ_CN5004.md` - Ακαδημαϊκή αναφορά για Turnitin
- [ ] `VIVA_VOCE_GUIDE.md` - Οδηγίες για Viva Voce
- [ ] `PROJECT_SUMMARY.md` - Σύνοψη εργασίας
- [ ] `CN5004_2025-26-Assignment.docx` - Εκφώνηση εργασίας
- [ ] `pom.xml` - Maven configuration
- [ ] `.gitignore` - Git ignore rules
- [ ] `create_submission.sh` - Script για δημιουργία ZIP

### Source Code
- [ ] `src/main/java/module-info.java`
- [ ] `src/main/java/com/clinicms/MainApp.java`
- [ ] `src/main/java/com/clinicms/model/Doctor.java`
- [ ] `src/main/java/com/clinicms/model/Specialist.java`
- [ ] `src/main/java/com/clinicms/model/Patient.java`
- [ ] `src/main/java/com/clinicms/model/Appointment.java`
- [ ] `src/main/java/com/clinicms/service/DataStore.java`
- [ ] `src/main/java/com/clinicms/controller/MainController.java`
- [ ] `src/main/java/com/clinicms/controller/DashboardController.java`
- [ ] `src/main/java/com/clinicms/controller/DoctorController.java`
- [ ] `src/main/java/com/clinicms/controller/PatientController.java`
- [ ] `src/main/java/com/clinicms/controller/AppointmentController.java`
- [ ] `src/main/java/com/clinicms/controller/ReportController.java`
- [ ] `src/main/java/com/clinicms/util/IdGenerator.java`
- [ ] `src/main/java/com/clinicms/util/Validator.java`

### Resources
- [ ] `src/main/resources/com/clinicms/fxml/MainView.fxml`
- [ ] `src/main/resources/com/clinicms/fxml/DashboardView.fxml`
- [ ] `src/main/resources/com/clinicms/fxml/DoctorView.fxml`
- [ ] `src/main/resources/com/clinicms/fxml/PatientView.fxml`
- [ ] `src/main/resources/com/clinicms/fxml/AppointmentView.fxml`
- [ ] `src/main/resources/com/clinicms/fxml/ReportView.fxml`
- [ ] `src/main/resources/com/clinicms/css/style.css`

### Data
- [ ] `data/doctors.csv` - Sample data
- [ ] `data/patients.csv` - Sample data
- [ ] `data/appointments.csv` - Sample data

### Build
- [ ] `target/ClinicMS-1.0-SNAPSHOT.jar` - Εκτελέσιμο JAR

---

## Υποβολή

### Turnitin (Ακαδημαϊκή Αναφορά)
- [ ] Ανοίξτε το `ΑΝΑΦΟΡΑ_CN5004.md`
- [ ] Αντιγράψτε το περιεχόμενο σε Word document
- [ ] Προσθέστε το επίσημο εξώφυλλο
- [ ] Προσθέστε το έγγραφο εκφώνησης
- [ ] Αποθηκεύστε ως PDF
- [ ] Ανεβάστε στο Turnitin link στο Moodle

### Moodle (Πηγαίος Κώδικας)
- [ ] Εκτελέστε το script: `./create_submission.sh`
- [ ] Ελέγξτε ότι το ZIP δημιουργήθηκε επιτυχώς
- [ ] Ανεβάστε το ZIP στο Moodle link για πηγαίο κώδικα

### Προαιρετικό (JAR)
- [ ] Το JAR περιλαμβάνεται στο ZIP
- [ ] Μπορεί να εκτελεστεί με: `java -jar ClinicMS-1.0-SNAPSHOT.jar`

---

## Προετοιμασία για Viva Voce

### Γνώση Κώδικα
- [ ] Διαβάστε το `VIVA_VOCE_GUIDE.md`
- [ ] Κατανοήστε όλες τις OOP αρχές που εφαρμόσατε
- [ ] Κατανοήστε τον αλγόριθμο αποτροπής διπλοκράτησης
- [ ] Κατανοήστε το File I/O mechanism
- [ ] Κατανοήστε το Singleton pattern
- [ ] Κατανοήστε το Validation mechanism

### Επίδειξη
- [ ] Εξασκηθείτε στην εκτέλεση της εφαρμογής
- [ ] Προετοιμάστε παραδείγματα για κάθε λειτουργία
- [ ] Προετοιμάστε παραδείγματα σφαλμάτων (διπλοκράτηση, validation)
- [ ] Προετοιμάστε εξήγηση για τις αποφάσεις σχεδιασμού

---

## Τελικός Έλεγχος

### Πριν την Υποβολή
- [ ] Το project κάνει compile
- [ ] Το project εκτελείται
- [ ] Όλες οι λειτουργίες δουλεύουν
- [ ] Η αναφορά είναι πλήρης
- [ ] Το ZIP δημιουργήθηκε επιτυχώς
- [ ] Έχετε backup του project

### Μετά την Υποβολή
- [ ] Επιβεβαιώστε ότι η αναφορά ανέβηκε στο Turnitin
- [ ] Επιβεβαιώστε ότι το ZIP ανέβηκε στο Moodle
- [ ] Κρατήστε αντίγραφο του ZIP
- [ ] Προετοιμαστείτε για το Viva Voce

---

## Ημερομηνίες

- **Ημερομηνία Ανάθεσης:** Week 4
- **Ημερομηνία Υποβολής:** Week 12
- **Viva Voce:** Θα ανακοινωθεί

---

**Καλή επιτυχία!**
