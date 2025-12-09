from dataclasses import dataclass
from typing import List, Dict

DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

TIME_SLOTS = [
    "08:30-09:20",
    "09:30-10:20",
    "10:30-11:20",
    "11:30-12:20",
    "12:30-13:20",
    "13:20-14:10",   # exam block
    "14:10-15:00",   # exam block
    "15:10-16:00",
    "16:10-17:00",
    "17:10-18:00",
]

FRIDAY_EXAM_SLOT_INDEXES = {5, 6}  # 13:20–15:10 on Fridays


@dataclass
class Course:
    code: str
    name: str
    year: int
    instructor: str
    theory_hours: int
    lab_hours: int
    is_elective: bool = False
    department: str = "CENG"
    capacity: int = 40


@dataclass
class Session:
    course_code: str
    instructor: str
    is_lab: bool
    year: int
    day: int          # 0..4
    slot_index: int   # 0..len(TIME_SLOTS)-1
    duration: int = 1
    room: str = ""
    capacity: int = 0


@dataclass
class Conflict:
    kind: str
    message: str


@dataclass
class ScheduleStats:
    total_courses: int
    theory_hours: int
    lab_hours: int
    classroom_utilization: int  # %
    schedule_efficiency: int    # %


# ---------- NEW: Instructor availability & classrooms ----------

@dataclass
class InstructorAvailability:
    name: str
    # day -> list of allowed slots (slot index)
    available: Dict[int, List[int]]


@dataclass
class Classroom:
    name: str
    kind: str      # "theory" or "lab"
    capacity: int
