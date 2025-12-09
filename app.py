import json
from flask import Flask, render_template, request, jsonify

from models import Course, InstructorAvailability, Classroom
from scheduler import BeeScheduler

app = Flask(__name__)

# --------- in-memory uploaded data ---------

UPLOADED_COURSES = None              # list[Course] | None
UPLOADED_INSTRUCTORS = None          # list[InstructorAvailability] | None
UPLOADED_CLASSROOMS = None           # list[Classroom] | None


# ---------- demo curriculum (fallback) ----------

def create_demo_courses():
    return [
        Course("CENG 101", "Intro to Computer Engineering", 1,
               "Dr. Smith", theory_hours=3, lab_hours=0, capacity=60),
        Course("CENG 101 Lab", "Intro to CE Lab", 1,
               "Dr. Smith", theory_hours=0, lab_hours=2, capacity=30),
        Course("MATH 101", "Calculus I", 1,
               "Dr. Johnson", theory_hours=3, lab_hours=0, capacity=80),
    ]


def get_courses():
    global UPLOADED_COURSES
    if UPLOADED_COURSES is not None:
        return UPLOADED_COURSES
    return create_demo_courses()


def get_instructors():
    global UPLOADED_INSTRUCTORS
    return UPLOADED_INSTRUCTORS or []


def get_classrooms():
    global UPLOADED_CLASSROOMS
    return UPLOADED_CLASSROOMS or []


# ---------- routes ----------

@app.route("/")
def index():
    return render_template("index.html")


@app.route("/api/generate_schedule")
def api_generate_schedule():
    max_theory = int(request.args.get("maxTheory", 4))
    max_lab_capacity = int(request.args.get("maxLabCapacity", 40))

    courses = get_courses()
    instructors = get_instructors()
    classrooms = get_classrooms()

    scheduler = BeeScheduler(
        courses,
        max_theory_per_day=max_theory,
        max_lab_capacity=max_lab_capacity,
        instructors=instructors,
        classrooms=classrooms,
    )

    sessions_by_year = scheduler.generate_demo_schedule()
    conflicts = scheduler.validate(sessions_by_year)
    stats = scheduler.compute_stats(sessions_by_year)

    sessions_json = {
        str(year): [
            {
                "course_code": s.course_code,
                "instructor": s.instructor,
                "is_lab": s.is_lab,
                "year": s.year,
                "day": s.day,
                "slot_index": s.slot_index,
                "duration": s.duration,
                "room": s.room,
                "capacity": s.capacity,
            }
            for s in sessions
        ]
        for year, sessions in sessions_by_year.items()
    }

    conflicts_json = [
        {"kind": c.kind, "message": c.message}
        for c in conflicts
    ]

    stats_json = {
        "total_courses": stats.total_courses,
        "theory_hours": stats.theory_hours,
        "lab_hours": stats.lab_hours,
        "classroom_utilization": stats.classroom_utilization,
        "schedule_efficiency": stats.schedule_efficiency,
    }

    return jsonify(
        sessions_by_year=sessions_json,
        conflicts=conflicts_json,
        stats=stats_json,
    )


# ---------- upload endpoints ----------

@app.route("/api/upload/curriculum", methods=["POST"])
def api_upload_curriculum():
    if "file" not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files["file"]
    if file.filename == "":
        return jsonify({"error": "No selected file"}), 400

    try:
        data = json.load(file.stream)
        courses: list[Course] = []
        for item in data:
            courses.append(
                Course(
                    code=item["code"],
                    name=item.get("name", item["code"]),
                    year=int(item.get("year", 1)),
                    instructor=item.get("instructor", "TBA"),
                    theory_hours=int(item.get("theory_hours", 0)),
                    lab_hours=int(item.get("lab_hours", 0)),
                    capacity=int(item.get("capacity", 40)),
                )
            )
    except Exception as e:
        return jsonify({"error": f"Failed to parse JSON: {e}"}), 400

    global UPLOADED_COURSES
    UPLOADED_COURSES = courses

    return jsonify({"status": "ok", "course_count": len(courses)})


@app.route("/api/upload/instructors", methods=["POST"])
def api_upload_instructors():
    """
    JSON format expectation:
    [
      {
        "name": "Dr. Smith",
        "available": [
          { "day": 0, "slots": [0,1,2,3] },
          { "day": 2, "slots": [0,1,2,3] }
        ]
      },
      ...
    ]
    """
    if "file" not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files["file"]
    try:
        raw = json.load(file.stream)
        instructors: list[InstructorAvailability] = []
        for item in raw:
            avail: Dict[int, list[int]] = {}
            for block in item.get("available", []):
                d = int(block["day"])
                slots = [int(s) for s in block.get("slots", [])]
                avail[d] = slots
            instructors.append(
                InstructorAvailability(
                    name=item["name"],
                    available=avail,
                )
            )
    except Exception as e:
        return jsonify({"error": f"Failed to parse JSON: {e}"}), 400

    global UPLOADED_INSTRUCTORS
    UPLOADED_INSTRUCTORS = instructors
    return jsonify({"status": "ok", "instructor_count": len(instructors)})


@app.route("/api/upload/classrooms", methods=["POST"])
def api_upload_classrooms():
    """
    JSON format expectation:
    [
      { "name": "A101", "kind": "theory", "capacity": 60 },
      { "name": "Lab A1", "kind": "lab", "capacity": 30 }
    ]
    """
    if "file" not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files["file"]
    try:
        raw = json.load(file.stream)
        rooms: list[Classroom] = []
        for item in raw:
            rooms.append(
                Classroom(
                    name=item["name"],
                    kind=item.get("kind", "theory"),
                    capacity=int(item.get("capacity", 40)),
                )
            )
    except Exception as e:
        return jsonify({"error": f"Failed to parse JSON: {e}"}), 400

    global UPLOADED_CLASSROOMS
    UPLOADED_CLASSROOMS = rooms
    return jsonify({"status": "ok", "room_count": len(rooms)})


if __name__ == "__main__":
    app.run(debug=True)
