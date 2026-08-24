export type IssueType = "STORY" | "TASK" | "BUG";
export type IssuePriority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";
export type SprintState = "PLANNED" | "ACTIVE" | "COMPLETED";
export type ProjectRole = "PROJECT_ADMIN" | "MEMBER" | "VIEWER";

export interface IssueCard {
  key: string;
  title: string;
  type: IssueType;
  priority: IssuePriority;
  assignee: string;
  epic?: string;
}

export interface BoardColumn {
  id: string;
  name: string;
  color: string;
  issues: IssueCard[];
}
