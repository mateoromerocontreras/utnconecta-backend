import "../styles/filter-bar.css";

export default function FilterBar({ children, actions, className = "" }) {
  const wrapperClass = ["filter-bar", className].filter(Boolean).join(" ");

  return (
    <div className={wrapperClass}>
      <div className="container filter-bar-inner">
        <div className="filter-bar-body">
          {children}
        </div>
        {actions && (
          <div className="filter-bar-actions">
            {actions}
          </div>
        )}
      </div>
    </div>
  );
}
