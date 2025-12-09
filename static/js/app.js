const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];
const TIME_SLOTS = [
  "08:30-09:20",
  "09:30-10:20",
  "10:30-11:20",
  "11:30-12:20",
  "12:30-13:20",
  "13:20-14:10",
  "14:10-15:00",
  "15:10-16:00",
  "16:10-17:00",
  "17:10-18:00"
];

let currentYear = 1;
let currentData = {
  sessions_by_year: { "1": [], "2": [], "3": [], "4": [] },
  conflicts: [],
  stats: null
};

// ---------- rendering helpers ----------

function renderSchedule() {
  const tbody = document.getElementById("scheduleBody");
  tbody.innerHTML = "";

  for (let r = 0; r < TIME_SLOTS.length; r++) {
    const tr = document.createElement("tr");

    const timeCell = document.createElement("td");
    timeCell.textContent = TIME_SLOTS[r];
    tr.appendChild(timeCell);

    for (let d = 0; d < DAYS.length; d++) {
      const td = document.createElement("td");
      tr.appendChild(td);
    }

    tbody.appendChild(tr);
  }

  const sessions = currentData.sessions_by_year[String(currentYear)] || [];
  for (const s of sessions) {
    for (let off = 0; off < s.duration; off++) {
      const row = s.slot_index + off;
      const col = s.day + 1;
      const tr = tbody.children[row];
      if (!tr) continue;
      const cell = tr.children[col];
      if (!cell) continue;

      cell.innerHTML = `${s.course_code}<br><span class="inst">${s.instructor}</span>`;
      cell.style.backgroundColor = s.is_lab ? "#fff7d6" : "#e7f2ff";
    }
  }
}

function renderConflicts() {
  const conflicts = currentData.conflicts || [];
  const summary = document.getElementById("conflictSummary");
  const list = document.getElementById("conflictList");
  list.innerHTML = "";

  const inst = conflicts.filter(c => c.kind.includes("INSTRUCTOR")).length;
  const cap = conflicts.filter(c => c.kind.includes("CAPACITY")).length;
  const rule = conflicts.filter(c => c.kind.includes("RULE")).length;

  summary.innerHTML = `
    <b>Total Conflicts:</b> ${conflicts.length}<br>
    Instructor Overlaps: ${inst}<br>
    Capacity Issues: ${cap}<br>
    Rule Violations: ${rule}
  `;

  for (const c of conflicts) {
    const div = document.createElement("div");
    div.className = "conflict-card";
    div.innerHTML = `
      <div class="conflict-card-title">${c.kind}</div>
      <div>${c.message}</div>
    `;
    list.appendChild(div);
  }
}

function renderStats() {
  const stats = currentData.stats;
  if (!stats) return;
  const box = document.getElementById("statsContainer");
  box.innerHTML = `
    <b>Total Courses Scheduled:</b> ${stats.total_courses}<br>
    <b>Theory Hours:</b> ${stats.theory_hours}<br>
    <b>Lab Hours:</b> ${stats.lab_hours}<br>
    <b>Classroom Utilization:</b> ${stats.classroom_utilization}%<br>
    <b>Schedule Efficiency:</b> ${stats.schedule_efficiency}%
  `;
}

// ---------- API ----------

async function fetchScheduleFromServer() {
  const maxTheory = document.getElementById("maxTheory").value || 4;
  const maxLab = document.getElementById("maxLabCapacity").value || 40;

  const url = `/api/generate_schedule?maxTheory=${encodeURIComponent(
    maxTheory
  )}&maxLabCapacity=${encodeURIComponent(maxLab)}`;

  const res = await fetch(url);
  const data = await res.json();
  currentData = data;

  renderSchedule();
  renderConflicts();
  renderStats();
}

// generic uploader
async function uploadFile(inputEl, url, afterSuccess) {
  const file = inputEl.files[0];
  if (!file) return;

  const formData = new FormData();
  formData.append("file", file);

  try {
    const res = await fetch(url, {
      method: "POST",
      body: formData
    });
    const data = await res.json();
    if (!res.ok) {
      alert(`Upload error: ${data.error || res.statusText}`);
      return;
    }
    console.log("Upload response:", data);
    alert("File uploaded successfully.");
    if (afterSuccess) {
      afterSuccess();
    }
  } catch (err) {
    console.error(err);
    alert("Upload failed.");
  } finally {
    // reset the input so selecting same file again will trigger change
    inputEl.value = "";
  }
}

// ---------- tabs, years, export ----------

function setupTabs() {
  const tabs = document.querySelectorAll(".tab");
  tabs.forEach(tab => {
    tab.addEventListener("click", () => {
      tabs.forEach(t => t.classList.remove("active"));
      tab.classList.add("active");

      const id = tab.dataset.tab;
      document.querySelectorAll(".tab-content").forEach(sec => {
        sec.classList.toggle("active", sec.id === `tab-${id}`);
      });
    });
  });
}

function setupYearButtons() {
  const yearButtons = document.querySelectorAll(".year-btn");
  yearButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      yearButtons.forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      currentYear = parseInt(btn.dataset.year, 10);
      renderSchedule();
    });
  });
}

function setupButtons() {
  const btnGenerate = document.getElementById("btnGenerate");
  const btnValidate = document.getElementById("btnValidate");
  const btnExport = document.getElementById("btnExport");

  btnGenerate.addEventListener("click", fetchScheduleFromServer);

  btnValidate.addEventListener("click", async () => {
    await fetchScheduleFromServer();
    document.querySelector('[data-tab="conflicts"]').click();
  });

  btnExport.addEventListener("click", () => {
    const blob = new Blob(
      [JSON.stringify(currentData.sessions_by_year, null, 2)],
      { type: "application/json" }
    );
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "schedule.json";
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  });

  // ---- upload buttons wiring ----

  const btnUploadCurriculum = document.getElementById("btnUploadCurriculum");
  const curriculumFile = document.getElementById("curriculumFile");
  btnUploadCurriculum.addEventListener("click", () => curriculumFile.click());
  curriculumFile.addEventListener("change", () =>
    uploadFile(curriculumFile, "/api/upload/curriculum", fetchScheduleFromServer)
  );

  const btnUploadInstructor = document.getElementById("btnUploadInstructor");
  const instructorFile = document.getElementById("instructorFile");
  btnUploadInstructor.addEventListener("click", () => instructorFile.click());
  instructorFile.addEventListener("change", () =>
    uploadFile(instructorFile, "/api/upload/instructors", null)
  );

  const btnUploadClassroom = document.getElementById("btnUploadClassroom");
  const classroomFile = document.getElementById("classroomFile");
  btnUploadClassroom.addEventListener("click", () => classroomFile.click());
  classroomFile.addEventListener("change", () =>
    uploadFile(classroomFile, "/api/upload/classrooms", null)
  );
}

// ---------- init ----------

window.addEventListener("DOMContentLoaded", () => {
  setupTabs();
  setupYearButtons();
  setupButtons();

  // initial schedule (demo or uploaded if you already did)
  fetchScheduleFromServer();
});
