CREATE TABLE IF NOT EXISTS role (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(255),
    role_description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS employee_role (
    employee_id INT,
    role_id INT,
    PRIMARY KEY (employee_id, role_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (role_id) REFERENCES role(role_id)
    );

CREATE TABLE IF NOT EXISTS project (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    project_title VARCHAR(255),
    project_description TEXT,
    project_start_date DATE,
    project_end_date DATE,
    project_customer VARCHAR(255),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id)
    );

CREATE TABLE IF NOT EXISTS project_employee (
    employee_id INT,
    project_id INT,
    PRIMARY KEY (employee_id, project_id),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (project_id) REFERENCES project(project_id)
    );

CREATE TABLE IF NOT EXISTS sub_project (
    sub_project_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT,
    sub_project_title VARCHAR(255),
    sub_project TEXT,
    sub_project_start_date DATE,
    sub_project_end_date DATE,
    sub_project_duration INT,
    FOREIGN KEY (project_id) REFERENCES project(project_id)
    );

CREATE TABLE IF NOT EXISTS task (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT,
    sub_project_id INT,
    task_title VARCHAR(255),
    task_description TEXT,
    task_status VARCHAR(255),
    task_start_date DATE,
    task_end_date DATE,
    task_duration INT,
    task_priority VARCHAR(255),
    task_note TEXT,
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    FOREIGN KEY (sub_project_id) REFERENCES sub_project(sub_project_id)
    );

CREATE TABLE IF NOT EXISTS sub_task (
    sub_task_id INT AUTO_INCREMENT PRIMARY KEY,
    task_id INT,
    sub_task_description TEXT,
    sub_task_status VARCHAR(255),
    sub_task_start_date DATE,
    sub_task_end_date DATE,
    sub_task_duration INT,
    FOREIGN KEY (task_id) REFERENCES task(task_id)
    );