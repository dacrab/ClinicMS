#!/bin/bash

# Script για δημιουργία ZIP αρχείου για υποβολή
# CN5004 - Προχωρημένος Προγραμματισμός

echo "=========================================="
echo "Δημιουργία ZIP αρχείου για υποβολή"
echo "CN5004 - Σύστημα Διαχείρισης Κλινικής"
echo "=========================================="
echo ""

# Καθαρισμός παλιών builds
echo "1. Καθαρισμός παλιών builds..."
mvn clean > /dev/null 2>&1

# Compile του project
echo "2. Compile του project..."
mvn compile > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ ΣΦΑΛΜΑ: Το project δεν κάνει compile!"
    echo "Παρακαλώ διορθώστε τα σφάλματα πριν τη δημιουργία του ZIP."
    exit 1
fi

echo "✅ Compile επιτυχής!"

# Δημιουργία JAR
echo "3. Δημιουργία JAR αρχείου..."
mvn package -DskipTests > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ ΣΦΑΛΜΑ: Αποτυχία δημιουργίας JAR!"
    exit 1
fi

echo "✅ JAR δημιουργήθηκε επιτυχώς!"

# Δημιουργία φακέλου για το ZIP
SUBMISSION_DIR="ClinicMS_Submission"
rm -rf "$SUBMISSION_DIR"
mkdir -p "$SUBMISSION_DIR"

# Αντιγραφή αρχείων
echo "4. Αντιγραφή αρχείων..."

# Source code
cp -r src "$SUBMISSION_DIR/"
cp pom.xml "$SUBMISSION_DIR/"
cp module-info.java "$SUBMISSION_DIR/" 2>/dev/null || true

# Documentation
cp README.md "$SUBMISSION_DIR/"
cp ΑΝΑΦΟΡΑ_CN5004.md "$SUBMISSION_DIR/"
cp VIVA_VOCE_GUIDE.md "$SUBMISSION_DIR/"
cp PROJECT_SUMMARY.md "$SUBMISSION_DIR/"

# Data (sample data)
cp -r data "$SUBMISSION_DIR/"

# JAR file
mkdir -p "$SUBMISSION_DIR/executable"
cp target/ClinicMS-1.0-SNAPSHOT.jar "$SUBMISSION_DIR/executable/"

# .gitignore
cp .gitignore "$SUBMISSION_DIR/" 2>/dev/null || true

echo "✅ Αρχεία αντιγράφηκαν!"

# Δημιουργία ZIP
echo "5. Δημιουργία ZIP αρχείου..."
ZIP_NAME="ClinicMS_CN5004_$(date +%Y%m%d).zip"
zip -r "$ZIP_NAME" "$SUBMISSION_DIR" > /dev/null 2>&1

if [ $? -ne 0 ]; then
    echo "❌ ΣΦΑΛΜΑ: Αποτυχία δημιουργίας ZIP!"
    exit 1
fi

# Καθαρισμός
rm -rf "$SUBMISSION_DIR"

echo "✅ ZIP δημιουργήθηκε επιτυχώς!"
echo ""
echo "=========================================="
echo "✅ ΟΛΟΚΛΗΡΩΘΗΚΕ ΕΠΙΤΥΧΩΣ!"
echo "=========================================="
echo ""
echo "Αρχείο: $ZIP_NAME"
echo "Μέγεθος: $(du -h "$ZIP_NAME" | cut -f1)"
echo ""
echo "Το αρχείο είναι έτοιμο για υποβολή στο Moodle!"
echo ""
echo "Επόμενα βήματα:"
echo "1. Ανεβάστε το ΑΝΑΦΟΡΑ_CN5004.md στο Turnitin"
echo "2. Ανεβάστε το $ZIP_NAME στο Moodle"
echo "3. Προετοιμαστείτε για το Viva Voce (δείτε VIVA_VOCE_GUIDE.md)"
echo ""
