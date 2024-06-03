-- Создание таблицы equipment
CREATE TABLE equipment (
serial_num VARCHAR(255) PRIMARY KEY,
equip_name VARCHAR(255),
equip_type VARCHAR(255),
condition VARCHAR(50),
details TEXT,
location VARCHAR(255)
);

-- Создание таблицы members
CREATE TABLE members (
id SERIAL PRIMARY KEY,
name VARCHAR(100),
phone VARCHAR(20),
e_mail VARCHAR(100),
login VARCHAR(50),
pass VARCHAR(50),
role VARCHAR(50)
);

-- Создание таблицы requests
CREATE TABLE requests (
id SERIAL PRIMARY KEY,
serial_num VARCHAR(255),
problem_desc TEXT,
request_comments TEXT,
status VARCHAR(50),
date_start DATE,
member_id INT,
CONSTRAINT fk_requests_serial_num FOREIGN KEY (serial_num) REFERENCES equipment(serial_num),
CONSTRAINT fk_requests_member_id FOREIGN KEY (member_id) REFERENCES members(id)
);

-- Создание таблицы reports с внешним ключом к requests (request_id)
CREATE TABLE reports (
request_id INT PRIMARY KEY REFERENCES requests(id),
repair_type VARCHAR(100),
time INT,
cost NUMERIC,
resources TEXT,
reason TEXT,
help TEXT
);

-- Создание таблицы orders с внешним ключом к requests (request_id)
CREATE TABLE orders (
id SERIAL PRIMARY KEY,
request_id INT REFERENCES requests(id),
resource_type VARCHAR(100),
resource_name VARCHAR(255),
cost NUMERIC
);



-- Функция для уведомления о создании заявки
CREATE OR REPLACE FUNCTION notify_request_created()
RETURNS trigger AS $$
BEGIN
PERFORM pg_notify('request_created', NEW.id::text);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для уведомления о создании заявки
CREATE TRIGGER trigger_request_created
AFTER INSERT ON requests
FOR EACH ROW EXECUTE FUNCTION notify_request_created();

-- Функция для уведомления об обновлении заявки
CREATE OR REPLACE FUNCTION notify_request_updated()
RETURNS trigger AS $$
BEGIN
PERFORM pg_notify('request_updated', NEW.id::text);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для уведомления об обновлении заявки
CREATE TRIGGER trigger_request_updated
AFTER UPDATE ON requests
FOR EACH ROW EXECUTE FUNCTION notify_request_updated();