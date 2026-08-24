create table users (
  id binary(16) primary key,
  display_name varchar(255) not null,
  email varchar(255) not null,
  password_hash varchar(255) not null,
  global_role varchar(40) not null,
  enabled bit not null,
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  constraint uk_users_email unique (email)
);

create table refresh_sessions (
  id binary(16) primary key,
  user_id binary(16) not null,
  token_hash varchar(255) not null,
  family_id binary(16) not null,
  expires_at datetime(6) not null,
  revoked_at datetime(6),
  replaced_by_id binary(16),
  user_agent varchar(255),
  ip_address varchar(64),
  constraint uk_refresh_token_hash unique (token_hash),
  constraint fk_refresh_user foreign key (user_id) references users (id)
);

create table projects (
  id binary(16) primary key,
  project_key varchar(12) not null,
  name varchar(255) not null,
  description varchar(2000),
  next_issue_number bigint not null,
  created_by binary(16) not null,
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  constraint uk_projects_key unique (project_key),
  constraint fk_projects_creator foreign key (created_by) references users (id)
);

create table project_members (
  project_id binary(16) not null,
  user_id binary(16) not null,
  role varchar(40) not null,
  joined_at datetime(6) not null,
  primary key (project_id, user_id),
  constraint fk_members_project foreign key (project_id) references projects (id),
  constraint fk_members_user foreign key (user_id) references users (id)
);

create table workflow_statuses (
  id binary(16) primary key,
  project_id binary(16) not null,
  name varchar(80) not null,
  category varchar(40) not null,
  display_order int not null,
  color varchar(32) not null,
  initial_status bit not null,
  terminal_status bit not null,
  constraint uk_status_name unique (project_id, name),
  constraint uk_status_order unique (project_id, display_order),
  constraint fk_status_project foreign key (project_id) references projects (id)
);

create table epics (
  id binary(16) primary key,
  project_id binary(16) not null,
  name varchar(255) not null,
  description varchar(2000),
  color varchar(32),
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  constraint uk_epics_project_name unique (project_id, name),
  constraint fk_epics_project foreign key (project_id) references projects (id)
);

create table sprints (
  id binary(16) primary key,
  project_id binary(16) not null,
  name varchar(255) not null,
  goal varchar(2000),
  state varchar(40) not null,
  start_date date,
  end_date date,
  active_slot int,
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  constraint uk_sprints_project_name unique (project_id, name),
  constraint uk_sprints_active unique (project_id, active_slot),
  constraint fk_sprints_project foreign key (project_id) references projects (id)
);

create table issues (
  id binary(16) primary key,
  project_id binary(16) not null,
  issue_number bigint not null,
  title varchar(255) not null,
  description varchar(5000),
  type varchar(40) not null,
  priority varchar(40) not null,
  status_id binary(16) not null,
  reporter_id binary(16) not null,
  assignee_id binary(16),
  epic_id binary(16),
  sprint_id binary(16),
  due_date date,
  board_position int not null,
  version bigint not null,
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  constraint uk_issue_number unique (project_id, issue_number),
  constraint fk_issues_project foreign key (project_id) references projects (id),
  constraint fk_issues_status foreign key (status_id) references workflow_statuses (id),
  constraint fk_issues_reporter foreign key (reporter_id) references users (id),
  constraint fk_issues_assignee foreign key (assignee_id) references users (id),
  constraint fk_issues_epic foreign key (epic_id) references epics (id) on delete set null,
  constraint fk_issues_sprint foreign key (sprint_id) references sprints (id) on delete set null
);

create table comments (
  id binary(16) primary key,
  issue_id binary(16) not null,
  author_id binary(16) not null,
  body varchar(5000) not null,
  created_at datetime(6) not null,
  updated_at datetime(6) not null,
  deleted_at datetime(6),
  constraint fk_comments_issue foreign key (issue_id) references issues (id),
  constraint fk_comments_author foreign key (author_id) references users (id)
);

create table activity_events (
  id binary(16) primary key,
  issue_id binary(16) not null,
  actor_id binary(16) not null,
  event_type varchar(80) not null,
  field_name varchar(80),
  old_value varchar(1000),
  new_value varchar(1000),
  occurred_at datetime(6) not null,
  constraint fk_activity_issue foreign key (issue_id) references issues (id),
  constraint fk_activity_actor foreign key (actor_id) references users (id)
);

create table notification_outbox (
  id binary(16) primary key,
  notification_type varchar(80) not null,
  recipient_email varchar(255) not null,
  subject varchar(255) not null,
  body_text varchar(5000) not null,
  issue_key varchar(40),
  issue_url varchar(500),
  status varchar(40) not null,
  attempts int not null,
  next_attempt_at datetime(6),
  last_error varchar(1000),
  created_at datetime(6) not null,
  sent_at datetime(6)
);

create index ix_issues_status_position on issues (project_id, status_id, board_position);
create index ix_issues_assignee_updated on issues (project_id, assignee_id, updated_at);
create index ix_issues_epic on issues (epic_id);
create index ix_issues_sprint on issues (sprint_id);
create index ix_comments_issue_created on comments (issue_id, created_at);
create index ix_activity_issue_occurred on activity_events (issue_id, occurred_at);
create index ix_outbox_status_next on notification_outbox (status, next_attempt_at);
