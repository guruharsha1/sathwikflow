import { DndContext, useDraggable, useDroppable, type DragEndEvent } from "@dnd-kit/core";
import { Plus, Search, SlidersHorizontal } from "lucide-react";
import { useMemo, useState } from "react";
import { Button } from "../../components/ui/Button";
import type { BoardColumn, IssueCard } from "../../types/domain";
import { mockBoard } from "./mockBoard";

export function BoardPage() {
  const [columns, setColumns] = useState<BoardColumn[]>(mockBoard);
  const [query, setQuery] = useState("");

  const visibleColumns = useMemo(() => {
    if (!query.trim()) return columns;
    const term = query.toLowerCase();
    return columns.map((column) => ({
      ...column,
      issues: column.issues.filter((issue) => `${issue.key} ${issue.title} ${issue.assignee}`.toLowerCase().includes(term))
    }));
  }, [columns, query]);

  function onDragEnd(event: DragEndEvent) {
    const issueKey = String(event.active.id);
    const targetColumnId = event.over?.id ? String(event.over.id) : null;
    if (!targetColumnId) return;

    let moved: IssueCard | undefined;
    const withoutMoved = columns.map((column) => ({
      ...column,
      issues: column.issues.filter((issue) => {
        if (issue.key === issueKey) {
          moved = issue;
          return false;
        }
        return true;
      })
    }));

    if (!moved) return;
    setColumns(withoutMoved.map((column) => column.id === targetColumnId ? { ...column, issues: [...column.issues, moved!] } : column));
  }

  return (
    <main className="shell">
      <aside className="sidebar">
        <div>
          <p className="eyebrow">SathwikFlow</p>
          <h1>JLT</h1>
        </div>
        <nav className="nav">
          <a className="active" href="/projects/JLT/board">Board</a>
          <a href="/projects/JLT/issues">Issues</a>
          <a href="/projects/JLT/planning">Planning</a>
          <a href="/projects/JLT/settings/members">Members</a>
        </nav>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <p className="eyebrow">Project board</p>
            <h2>JIRA Lite MVP</h2>
          </div>
          <Button>
            <Plus size={18} />
            Issue
          </Button>
        </header>

        <div className="toolbar">
          <label className="search">
            <Search size={18} />
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search issues" />
          </label>
          <Button variant="ghost" aria-label="Filters">
            <SlidersHorizontal size={18} />
          </Button>
        </div>

        <DndContext onDragEnd={onDragEnd}>
          <div className="board" aria-label="Kanban board">
            {visibleColumns.map((column) => <BoardColumnView column={column} key={column.id} />)}
          </div>
        </DndContext>
      </section>
    </main>
  );
}

function BoardColumnView({ column }: { column: BoardColumn }) {
  const { setNodeRef, isOver } = useDroppable({ id: column.id });

  return (
    <section className={`column ${isOver ? "column-over" : ""}`} ref={setNodeRef}>
      <div className="column-title">
        <span style={{ backgroundColor: column.color }} />
        <h3>{column.name}</h3>
        <strong>{column.issues.length}</strong>
      </div>
      <div className="cards">
        {column.issues.map((issue) => <IssueCardView issue={issue} key={issue.key} />)}
      </div>
    </section>
  );
}

function IssueCardView({ issue }: { issue: IssueCard }) {
  const { attributes, listeners, setNodeRef, transform, isDragging } = useDraggable({ id: issue.key });
  const style = transform ? { transform: `translate3d(${transform.x}px, ${transform.y}px, 0)` } : undefined;

  return (
    <article
      className={`issue-card ${isDragging ? "issue-card-dragging" : ""}`}
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
    >
      <div className="issue-card-head">
        <span>{issue.key}</span>
        <b>{issue.priority}</b>
      </div>
      <h4>{issue.title}</h4>
      <footer>
        <span>{issue.type}</span>
        <span>{issue.assignee}</span>
      </footer>
    </article>
  );
}
