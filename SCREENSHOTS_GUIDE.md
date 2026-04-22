# Screenshots Guide

## Οδηγίες για Screenshots

Για την ολοκλήρωση της αναφοράς, χρειάζονται screenshots από τις παρακάτω οθόνες:

### 1. Dashboard (Πίνακας Ελέγχου)
- Εκτέλεση: `mvn javafx:run`
- Αρχική οθόνη με στατιστικά
- Screenshot: `screenshots/01_dashboard.png`

### 2. Διαχείριση Ιατρών
- Κλικ στο "Ιατροί" στην πλαϊνή μπάρα
- Εμφάνιση πίνακα με ιατρούς και φόρμα προσθήκης
- Screenshot: `screenshots/02_doctors.png`

### 3. Διαχείριση Ασθενών
- Κλικ στο "Ασθενείς"
- Εμφάνιση πίνακα με ασθενείς και φόρμα εγγραφής
- Screenshot: `screenshots/03_patients.png`

### 4. Διαχείριση Ραντεβού
- Κλικ στο "Ραντεβού"
- Εμφάνιση πίνακα ραντεβού και φόρμα προγραμματισμού
- Screenshot: `screenshots/04_appointments.png`

### 5. Αναφορές
- Κλικ στο "Αναφορές"
- Εμφάνιση φίλτρων και πίνακα αποτελεσμάτων
- Screenshot: `screenshots/05_reports.png`

### 6. Validation Error (προαιρετικό)
- Προσπάθεια προσθήκης ιατρού χωρίς όνομα
- Εμφάνιση error dialog
- Screenshot: `screenshots/06_validation_error.png`

### 7. Double Booking Prevention (προαιρετικό)
- Προσπάθεια προγραμματισμού ραντεβού σε κατειλημμένη ώρα
- Εμφάνιση conflict warning
- Screenshot: `screenshots/07_conflict_warning.png`

## Πώς να πάρετε Screenshots

### Linux
```bash
# Δημιουργία φακέλου
mkdir -p screenshots

# Χρήση screenshot tool (π.χ. gnome-screenshot, flameshot)
gnome-screenshot -w  # για το ενεργό παράθυρο
```

### Windows
- `Win + Shift + S` για Snipping Tool
- Ή `Alt + PrtScn` για το ενεργό παράθυρο

### macOS
- `Cmd + Shift + 4` και πατήστε `Space` για screenshot παραθύρου

## Ενσωμάτωση στην Αναφορά

Μετά τη λήψη των screenshots, προσθέστε τα στην αναφορά:

```markdown
### Screenshot 1: Dashboard
![Dashboard](screenshots/01_dashboard.png)

### Screenshot 2: Διαχείριση Ιατρών
![Doctors](screenshots/02_doctors.png)

... κ.λπ.
```

## Σημειώσεις

- Τα screenshots πρέπει να είναι καθαρά και ευανάγνωστα
- Προτιμήστε μέγεθος 1280x720 ή μεγαλύτερο
- Format: PNG (καλύτερη ποιότητα) ή JPG
- Βεβαιωθείτε ότι φαίνονται sample data στους πίνακες
