INSERT INTO TAGS(id, created_date, modified_date, title)
VALUES (2, current_timestamp, current_timestamp, 'Scrum');

INSERT INTO FILES_TAGS(id, created_date, file_id, tag_id)
VALUES (3, current_timestamp, 1, 2);

INSERT INTO TAGS(id, created_date, modified_date, title)
VALUES (4, current_timestamp, current_timestamp, 'Scrum Master');