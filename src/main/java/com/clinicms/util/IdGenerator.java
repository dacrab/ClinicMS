package com.clinicms.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple auto-incrementing ID generator for each entity type.
 * Counters are reset/initialised from the loaded data on startup.
 */
public class IdGenerator {

    private static final AtomicInteger doctorCounter    = new AtomicInteger(0);
    private static final AtomicInteger patientCounter   = new AtomicInteger(0);
    private static final AtomicInteger appointmentCounter = new AtomicInteger(0);

    /** Returns the next unique doctor ID. */
    public static int nextDoctorId() {
        return doctorCounter.incrementAndGet();
    }

    /** Returns the next unique patient ID. */
    public static int nextPatientId() {
        return patientCounter.incrementAndGet();
    }

    /** Returns the next unique appointment ID. */
    public static int nextAppointmentId() {
        return appointmentCounter.incrementAndGet();
    }

    /** Seeds the doctor counter so new IDs don't collide with existing records. */
    public static void seedDoctorId(int maxExistingId) {
        doctorCounter.set(Math.max(doctorCounter.get(), maxExistingId));
    }

    /** Seeds the patient counter. */
    public static void seedPatientId(int maxExistingId) {
        patientCounter.set(Math.max(patientCounter.get(), maxExistingId));
    }

    /** Seeds the appointment counter. */
    public static void seedAppointmentId(int maxExistingId) {
        appointmentCounter.set(Math.max(appointmentCounter.get(), maxExistingId));
    }

    private IdGenerator() {} // utility class – no instantiation
}
