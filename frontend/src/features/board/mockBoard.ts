import type { BoardColumn } from "../../types/domain";

export const mockBoard: BoardColumn[] = [
  {
    id: "backlog",
    name: "Backlog",
    color: "#64748b",
    issues: [
      { key: "JLT-7", title: "Implement frontend auth lifecycle", type: "STORY", priority: "HIGH", assignee: "Sathwik" },
      { key: "JLT-12", title: "List and filter project issues", type: "TASK", priority: "MEDIUM", assignee: "Unassigned" }
    ]
  },
  {
    id: "todo",
    name: "To Do",
    color: "#0f766e",
    issues: [
      { key: "JLT-11", title: "Allocate issue keys inside a project lock", type: "BUG", priority: "CRITICAL", assignee: "API" }
    ]
  },
  {
    id: "progress",
    name: "In Progress",
    color: "#b45309",
    issues: [
      { key: "JLT-15", title: "Load and render Kanban board", type: "STORY", priority: "HIGH", assignee: "Frontend", epic: "Board MVP" }
    ]
  },
  {
    id: "review",
    name: "In Review",
    color: "#7c3aed",
    issues: []
  },
  {
    id: "done",
    name: "Done",
    color: "#15803d",
    issues: [
      { key: "JLT-1", title: "Bootstrap API and SPA structure", type: "TASK", priority: "LOW", assignee: "Sathwik" }
    ]
  }
];
