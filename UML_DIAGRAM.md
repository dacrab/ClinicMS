# UML Class Diagram - Σύστημα Διαχείρισης Κλινικής

## Διάγραμμα Κλάσεων

```
┌─────────────────────────────────────────────────────────────────────────┐
│                            MODEL LAYER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────┐                                                    │
│  │     Doctor       │                                                    │
│  ├──────────────────┤                                                    │
│  │ - id: String     │                                                    │
│  │ - name: String   │                                                    │
│  │ - specialty: String                                                   │
│  │ - phone: String  │                                                    │
│  │ - email: String  │                                                    │
│  ├──────────────────┤                                                    │
│  │ + getId()        │                                                    │
│  │ + getName()      │                                                    │
│  │ + setName()      │                                                    │
│  │ + toCSV()        │                                                    │
│  │ + fromCSV()      │                                                    │
│  └──────────────────┘                                                    │
│           △                                                              │
│           │                                                              │
│           │ extends                                                      │
│           │                                                              │
│  ┌──────────────────┐                                                    │
│  │   Specialist     │                                                    │
│  ├──────────────────┤                                                    │
│  │ - certificationYear: int                                              │
│  ├──────────────────┤                                                    │
│  │ + getCertificationYear()                                              │
│  │ + setCertificationYear()                                              │
│  └──────────────────┘                                                    │
│                                                                           │
│  ┌──────────────────┐         ┌──────────────────┐                      │
│  │     Patient      │         │   Appointment    │                      │
│  ├──────────────────┤         ├──────────────────┤                      │
│  │ - id: String     │         │ - id: String     │                      │
│  │ - name: String   │         │ - patientId: String                     │
│  │ - dateOfBirth: String      │ - doctorId: String                      │
│  │ - gender: String │         │ - date: String   │                      │
│  │ - phone: String  │         │ - timeSlot: String                      │
│  │ - email: String  │         │ - reason: String │                      │
│  │ - medicalHistory: String   │ - status: Status │◄────┐               │
│  ├──────────────────┤         │ - notes: String  │     │               │
│  │ + getId()        │         ├──────────────────┤     │               │
│  │ + getName()      │         │ + getId()        │     │               │
│  │ + setName()      │         │ + getStatus()    │     │               │
│  │ + toCSV()        │         │ + setStatus()    │     │               │
│  │ + fromCSV()      │         │ + toCSV()        │     │               │
│  └──────────────────┘         │ + fromCSV()      │     │               │
│                                └──────────────────┘     │               │
│                                                          │               │
│                                                  ┌───────────────┐      │
│                                                  │  <<enum>>     │      │
│                                                  │    Status     │      │
│                                                  ├───────────────┤      │
│                                                  │  SCHEDULED    │      │
│                                                  │  COMPLETED    │      │
│                                                  │  CANCELLED    │      │
│                                                  └───────────────┘      │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                          SERVICE LAYER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────────────────────────────────────────┐               │
│  │         DataStore <<Singleton>>                      │               │
│  ├──────────────────────────────────────────────────────┤               │
│  │ - {static} instance: DataStore                       │               │
│  │ - doctors: List<Doctor>                              │               │
│  │ - patients: List<Patient>                            │               │
│  │ - appointments: List<Appointment>                    │               │
│  ├──────────────────────────────────────────────────────┤               │
│  │ + {static} getInstance(): DataStore                  │               │
│  │ + getDoctors(): List<Doctor>                         │               │
│  │ + getPatients(): List<Patient>                       │               │
│  │ + getAppointments(): List<Appointment>               │               │
│  │ + addDoctor(doctor): void                            │               │
│  │ + updateDoctor(doctor): void                         │               │
│  │ + deleteDoctor(id): boolean                          │               │
│  │ + addPatient(patient): void                          │               │
│  │ + updatePatient(patient): void                       │               │
│  │ + deletePatient(id): boolean                         │               │
│  │ + addAppointment(appointment): void                  │               │
│  │ + updateAppointment(appointment): void               │               │
│  │ + deleteAppointment(id): void                        │               │
│  │ + hasConflict(doctorId, date, timeSlot): boolean     │               │
│  │ + hasActiveAppointments(doctorId, patientId): boolean│               │
│  │ - loadDoctors(): void                                │               │
│  │ - loadPatients(): void                               │               │
│  │ - loadAppointments(): void                           │               │
│  │ - saveDoctors(): void                                │               │
│  │ - savePatients(): void                               │               │
│  │ - saveAppointments(): void                           │               │
│  └──────────────────────────────────────────────────────┘               │
│                    │                                                     │
│                    │ manages                                             │
│                    ▼                                                     │
│         ┌──────────────────────┐                                        │
│         │  Doctor, Patient,    │                                        │
│         │  Appointment objects  │                                        │
│         └──────────────────────┘                                        │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                          UTILITY LAYER                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────────────┐      ┌──────────────────────────┐        │
│  │     IdGenerator          │      │      Validator           │        │
│  ├──────────────────────────┤      ├──────────────────────────┤        │
│  │ - {static} doctorCounter │      │ + {static} isValidDate() │        │
│  │ - {static} patientCounter│      │ + {static} isValidTime() │        │
│  │ - {static} appointmentCounter   │ + {static} isValidEmail()│        │
│  ├──────────────────────────┤      │ + {static} isValidPhone()│        │
│  │ + {static} generateDoctorId()   │ + {static} isNotEmpty()  │        │
│  │ + {static} generatePatientId()  └──────────────────────────┘        │
│  │ + {static} generateAppointmentId()                                   │
│  └──────────────────────────┘                                           │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        CONTROLLER LAYER                                  │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────┐                                                    │
│  │  MainController  │                                                    │
│  ├──────────────────┤                                                    │
│  │ + showDashboard()│───┐                                               │
│  │ + showDoctors()  │   │                                               │
│  │ + showPatients() │   │                                               │
│  │ + showAppointments()  │                                               │
│  │ + showReports()  │   │                                               │
│  └──────────────────┘   │                                               │
│                          │ loads                                         │
│                          ▼                                               │
│  ┌─────────────────────────────────────────────────────┐                │
│  │  DashboardController                                │                │
│  │  DoctorController                                   │                │
│  │  PatientController                                  │                │
│  │  AppointmentController                              │                │
│  │  ReportController                                   │                │
│  └─────────────────────────────────────────────────────┘                │
│                          │                                               │
│                          │ uses                                          │
│                          ▼                                               │
│                    DataStore                                             │
│                    IdGenerator                                           │
│                    Validator                                             │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        APPLICATION ENTRY                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌──────────────────┐                                                    │
│  │     MainApp      │                                                    │
│  ├──────────────────┤                                                    │
│  │ + start(stage)   │                                                    │
│  │ + main(args)     │                                                    │
│  └──────────────────┘                                                    │
│           │                                                              │
│           │ creates                                                      │
│           ▼                                                              │
│    MainController                                                        │
└─────────────────────────────────────────────────────────────────────────┘
```

## Βασικές Σχέσεις

1. **Inheritance (Κληρονομικότητα)**
   - `Specialist extends Doctor` - Ο Specialist είναι ειδική περίπτωση Doctor

2. **Composition (Σύνθεση)**
   - `Appointment` περιέχει `Status` enum

3. **Association (Συσχέτιση)**
   - `DataStore` διαχειρίζεται λίστες από `Doctor`, `Patient`, `Appointment`
   - `Appointment` αναφέρεται σε `Doctor` και `Patient` μέσω IDs

4. **Dependency (Εξάρτηση)**
   - Controllers χρησιμοποιούν `DataStore`, `IdGenerator`, `Validator`
   - `MainController` φορτώνει τους άλλους controllers

5. **Singleton Pattern**
   - `DataStore` υλοποιεί το Singleton pattern για κεντρική διαχείριση δεδομένων

## Αρχές OOP που Εφαρμόζονται

- **Encapsulation**: Όλα τα πεδία private με public getters/setters
- **Inheritance**: Specialist κληρονομεί από Doctor
- **Polymorphism**: List<Doctor> μπορεί να περιέχει Specialist objects
- **Abstraction**: Διαχωρισμός σε layers (Model, Service, Controller, Utility)
- **Single Responsibility**: Κάθε κλάση έχει συγκεκριμένο ρόλο
