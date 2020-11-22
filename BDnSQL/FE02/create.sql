-- -----------------------------------------------------
-- Schema cardiac_sensor
-- -----------------------------------------------------
CREATE TABLESPACE cardiac_sensor DATAFILE 'CARDIAC_SENSOR_FILES_01.dbf' SIZE 500m;
CREATE USER cardiac_sensor IDENTIFIED BY cardiac_sensor DEFAULT TABLESPACE cardiac_sensor QUOTA UNLIMITED ON cardiac_sensor;
GRANT CONNECT, RESOURCE, CREATE VIEW ,CREATE SEQUENCE TO cardiac_sensor;

-- -----------------------------------------------------
-- Table cardiac_sensor.patient
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.patient (
    id NUMBER(10,0) NOT NULL ENABLE,
    name VARCHAR2(200 BYTE) NOT NULL ENABLE,
    birth_date DATE NOT NULL ENABLE,
    CONSTRAINT pk_patient PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table cardiac_sensor.doctor
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.doctor (
    id NUMBER(10,0) NOT NULL ENABLE,
    name VARCHAR2(200 BYTE) NOT NULL ENABLE,
    CONSTRAINT pk_doctor PRIMARY KEY (id));

-- -----------------------------------------------------
-- Table cardiac_sensor.service
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.service (
    codigo VARCHAR2(20 BYTE) NOT NULL ENABLE,
    descricao VARCHAR2(200 BYTE) NOT NULL ENABLE,
    CONSTRAINT pk_service PRIMARY KEY (codigo));

-- -----------------------------------------------------
-- Table cardiac_sensor.sensor
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.sensor (
    id NUMBER(10,0) NOT NULL ENABLE,
    num NUMBER(10,0) NOT NULL ENABLE,
    type VARCHAR2(200 BYTE) NOT NULL ENABLE,
    CONSTRAINT pk_cardiac_sensor PRIMARY KEY (id));


-- -----------------------------------------------------
-- Table cardiac_sensor.internment
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.internment (
    id NUMBER(10,0) NOT NULL ENABLE,
    patient NUMBER(10,0) NOT NULL ENABLE,
    service VARCHAR2(20 BYTE) NOT NULL ENABLE,
    admdate DATE NOT NULL ENABLE,
    bed NUMBER(10,0) NOT NULL ENABLE,
    CONSTRAINT pk_internment PRIMARY KEY (id),
    CONSTRAINT fk_i_patient FOREIGN KEY (patient)
        REFERENCES cardiac_sensor.patient (id),
    CONSTRAINT fk_i_service FOREIGN KEY (service)
        REFERENCES cardiac_sensor.service (codigo));


-- -----------------------------------------------------
-- Table cardiac_sensor.cardiac_sensor_reads
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.sensor_reads (
    internment NUMBER(10,0) NOT NULL ENABLE,
    sensor NUMBER(10,0) NOT NULL ENABLE,
    bodytemp NUMBER(4,0) NOT NULL ENABLE,
    systolic_blood_press NUMBER(4,0) NOT NULL ENABLE,
    diastolic_blood_press NUMBER(4,0) NOT NULL ENABLE,
    bpm NUMBER(4,0) NOT NULL ENABLE,
    sato2 NUMBER(4,0) NOT NULL ENABLE,
    timestamp TIMESTAMP NOT NULL ENABLE,
    CONSTRAINT fk_sr_internment FOREIGN KEY (internment)
        REFERENCES cardiac_sensor.internment (id),
    CONSTRAINT fk_sr_sensor FOREIGN KEY (sensor)
        REFERENCES cardiac_sensor.sensor (id));

-- -----------------------------------------------------
-- Table cardiac_sensor.patient_doctor
-- -----------------------------------------------------
CREATE TABLE cardiac_sensor.internment_doctor (
    internment NUMBER(10,0) NOT NULL ENABLE,
    doctor NUMBER(10,0) NOT NULL ENABLE,
    CONSTRAINT pk_intdoc PRIMARY KEY (internment, doctor),
    CONSTRAINT fk_id_internment FOREIGN KEY (internment) 
        REFERENCES cardiac_sensor.internment (id),
    CONSTRAINT dk_id_doctor FOREIGN KEY (doctor) 
        REFERENCES cardiac_sensor.doctor (id));