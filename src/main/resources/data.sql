CREATE
OR REPLACE FUNCTION update_change_date()
RETURNS TRIGGER AS $$
BEGIN
    NEW.change_date
= CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TABLE project
(
    project_id   BIGSERIAL PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL
);

CREATE TABLE project_version
(
    project_version_id        BIGSERIAL PRIMARY KEY,
    project_id                BIGINT       NOT NULL,
    parent_project_version_id BIGINT,
    project_version_name      VARCHAR(255) NOT NULL,
    last_merge_date           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_date               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_date               TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE cascade
);


CREATE TRIGGER trigger_update_project_version_change_date
    BEFORE UPDATE
    ON project_version
    FOR EACH ROW
    EXECUTE FUNCTION update_change_date();

CREATE TABLE localization
(
    localization_id BIGSERIAL PRIMARY KEY,
    file            VARCHAR(1000) NOT NULL,
    const           VARCHAR(500)  NOT NULL,
    key_loc         INTEGER       NOT NULL,
    default_loc     VARCHAR(1000) NOT NULL,
    create_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TRIGGER trigger_update_localization_change_date
    BEFORE UPDATE
    ON localization
    FOR EACH ROW
    EXECUTE FUNCTION update_change_date();

CREATE TABLE project_version_localization
(
    project_version_localization_id BIGSERIAL PRIMARY KEY,
    project_version_id              BIGINT NOT NULL,
    localization_id                 BIGINT NOT NULL,
    CONSTRAINT fk_project_version FOREIGN KEY (project_version_id) REFERENCES project_version (project_version_id) ON DELETE CASCADE,
    CONSTRAINT fk_localization FOREIGN KEY (localization_id) REFERENCES localization (localization_id) ON DELETE CASCADE
);

CREATE TABLE bundle
(
    bundle_id   BIGSERIAL PRIMARY KEY,
    bundle_name VARCHAR(255) NOT NULL
);

CREATE TABLE project_version_localization_bundle
(
    project_version_localization_bundle_id BIGSERIAL PRIMARY KEY,
    project_version_localization_id        BIGINT NOT NULL,
    bundle_id                              BIGINT NOT NULL,
    CONSTRAINT fk_project_version_localization FOREIGN KEY (project_version_localization_id) REFERENCES project_version_localization (project_version_localization_id) ON DELETE CASCADE,
    CONSTRAINT fk_bundle FOREIGN KEY (bundle_id) REFERENCES bundle (bundle_id) ON DELETE CASCADE
);

CREATE TABLE language_t
(
    language_id   BIGSERIAL PRIMARY KEY,
    language_name VARCHAR(50) NOT NULL,
    language_iso  VARCHAR(2)  NOT NULL
);



CREATE TABLE language_project
(
    language_project_id BIGSERIAL PRIMARY KEY,
    language_id         BIGINT NOT NULL,
    project_id          BIGINT NOT NULL,
    CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES language_t (language_id) ON DELETE CASCADE,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE localization_translation
(
    localization_translation_id              BIGSERIAL PRIMARY KEY,
    localization_id                          BIGINT        NOT NULL,
    language_id                              BIGINT        NOT NULL,
    last_history_localization_translation_id BIGINT,
    translation_value                        VARCHAR(1000) NOT NULL,
    create_date                              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    change_date                              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_localication FOREIGN KEY (localization_id) REFERENCES localization (localization_id) ON DELETE CASCADE,
    CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES language_t (language_id) ON DELETE CASCADE
);


CREATE TRIGGER trigger_update_localization_translation_change_date
    BEFORE UPDATE
    ON localization_translation
    FOR EACH ROW
    EXECUTE FUNCTION update_change_date();

CREATE TABLE history_localization_translation
(
    history_localization_translation_id BIGSERIAL PRIMARY KEY,
    localization_translation_id         BIGINT        NOT NULL,
    translation_value                   VARCHAR(1000) NOT NULL,
    create_date                         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_localication_translation FOREIGN KEY (localization_translation_id) REFERENCES localization_translation (localization_translation_id) ON DELETE CASCADE
);

CREATE TABLE user_t
(
    user_id         BIGSERIAL PRIMARY KEY,
    login_name      VARCHAR(255) NOT NULL,
    user_name       VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    hashed_password VARCHAR(255) NOT NULL
);

CREATE TABLE user_role
(
    user_id BIGINT      NOT NULL,
    role    VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_t (user_id) ON DELETE CASCADE
);

CREATE TABLE user_project
(
    user_project_id BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    project_id      BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_t (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
);

CREATE TABLE translator
(
    translator_id BIGSERIAL PRIMARY KEY,
    user_id       BIGINT NOT NULL,
    language_id   BIGINT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES user_t (user_id) ON DELETE cascade,
    CONSTRAINT fk_language FOREIGN KEY (language_id) REFERENCES language_t (language_id) ON DELETE CASCADE
);

insert into user_t (login_name, user_name, email, hashed_password)
values ('user', 'John Normal', 'john.normal@gmail.com', '$2a$10$xdbKoM48VySZqVSU/cSlVeJn0Z04XCZ7KZBjUBC00eKo5uLswyOpe');
insert into user_t (login_name, user_name, email, hashed_password)
values ('admin', 'Emma Executive', 'emma.executive@gmail.com', '$2a$10$jpLNVNeA7Ar/ZQ2DKbKCm.MuT2ESe.Qop96jipKMq7RaUgCoQedV.');


insert into user_role (user_id, role)
values ('1', 'USER');
insert into user_role (user_id, role)
values ('2', 'ADMIN');