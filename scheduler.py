from typing import List, Dict, Set, Optional

from models import (
    Course,
    Session,
    Conflict,
    ScheduleStats,
    DAYS,
    TIME_SLOTS,
    FRIDAY_EXAM_SLOT_INDEXES,
    InstructorAvailability,
    Classroom,
)


class BeeScheduler:
    def __init__(
        self,
        courses: List[Course],
        max_theory_per_day: int,
        max_lab_capacity: int,
        instructors: Optional[List[InstructorAvailability]] = None,
        classrooms: Optional[List[Classroom]] = None,
    ):
        """
        courses: curriculum
        instructors: availability information (optional)
        classrooms: list of classrooms with capacities (optional)
        """
        self.courses = courses
        self.max_theory_per_day = max_theory_per_day
        self.max_lab_capacity = max_lab_capacity

        self.instructors_by_name: Dict[str, InstructorAvailability] = {
            inst.name: inst for inst in (instructors or [])
        }
        self.classrooms: List[Classroom] = classrooms or []

    # ------------------------------------------------------------------
    #  AVAILABILITY HELPERS
    # ------------------------------------------------------------------

    def is_instructor_available(self, name: str, day: int, slot: int) -> bool:
        inst = self.instructors_by_name.get(name)
        if inst is None:
            # instructor data yoksa serbest say
            return True
        slots = inst.available.get(day)
        if slots is None:
            # o gün için bilgi yoksa tüm slotlar serbest say
            return True
        return slot in slots

    def find_available_room(
        self,
        kind: str,
        capacity_needed: int,
        day: int,
        start_slot: int,
        hours_needed: int,
        room_occupied: Set[tuple],
    ) -> Optional[str]:
        """
        Uygun türde ve kapasitede, belirtilen süre boyunca boş olan bir sınıf bulur.
        Dönen değer: room_name veya None.
        """
        for room in self.classrooms:
            if room.kind != kind:
                continue
            if room.capacity < capacity_needed:
                continue

            ok = True
            for h in range(hours_needed):
                if (room.name, day, start_slot + h) in room_occupied:
                    ok = False
                    break
            if ok:
                return room.name
        return None

    # ------------------------------------------------------------------
    #  SCHEDULING
    # ------------------------------------------------------------------

    def find_consecutive_slots(
        self,
        sessions: List[Session],
        hours_needed: int,
        course: Course,
        is_lab: bool,
        room_occupied: Set[tuple],
    ):
        """
        Aynı günde 'hours_needed' kadar ardışık boş slot arar.
        Eğitmen uygunluğunu ve sınıf doluluğunu da kontrol eder.
        Dönüş: (day, start_slot, room_name) veya None
        """
        occupied = {(s.day, s.slot_index) for s in sessions}
        kind = "lab" if is_lab else "theory"

        for day in range(5):  # Monday–Friday
            for slot in range(len(TIME_SLOTS) - hours_needed + 1):
                # önce slotların tamamen boş ve eğitmene uygun olduğuna bakalım
                ok = True
                for h in range(hours_needed):
                    if (day, slot + h) in occupied:
                        ok = False
                        break
                    if not self.is_instructor_available(course.instructor, day, slot + h):
                        ok = False
                        break
                if not ok:
                    continue

                # ardından sınıf bul
                room_name = self.find_available_room(
                    kind,
                    course.capacity,
                    day,
                    slot,
                    hours_needed,
                    room_occupied,
                )
                if room_name is None:
                    continue

                return day, slot, room_name

        return None

    def generate_demo_schedule(self) -> Dict[int, List[Session]]:
        """
        Basit otomatik scheduler:

        - Her ders için:
          * theory_hours kadar ardışık blok
          * lab_hours kadar ardışık blok
        - Eğitmen uygunlukları ve sınıf dolulukları dikkate alınır.
        """
        sessions_by_year: Dict[int, List[Session]] = {1: [], 2: [], 3: [], 4: []}
        room_occupied: Set[tuple] = set()  # (room_name, day, slot)

        for course in self.courses:
            year = course.year if 1 <= course.year <= 4 else 1
            sessions = sessions_by_year[year]

            # --- THEORY block ---
            if course.theory_hours > 0:
                result = self.find_consecutive_slots(
                    sessions,
                    course.theory_hours,
                    course,
                    is_lab=False,
                    room_occupied=room_occupied,
                )
                if result:
                    day, start, room_name = result
                    for i in range(course.theory_hours):
                        slot = start + i
                        sessions.append(
                            Session(
                                course_code=course.code,
                                instructor=course.instructor,
                                is_lab=False,
                                year=year,
                                day=day,
                                slot_index=slot,
                                duration=1,
                                room=room_name,
                                capacity=course.capacity,
                            )
                        )
                        room_occupied.add((room_name, day, slot))

            # --- LAB block ---
            if course.lab_hours > 0:
                result = self.find_consecutive_slots(
                    sessions,
                    course.lab_hours,
                    course,
                    is_lab=True,
                    room_occupied=room_occupied,
                )
                if result:
                    day, start, room_name = result
                    for i in range(course.lab_hours):
                        slot = start + i
                        sessions.append(
                            Session(
                                course_code=course.code,
                                instructor=course.instructor,
                                is_lab=True,
                                year=year,
                                day=day,
                                slot_index=slot,
                                duration=1,
                                room=room_name,
                                capacity=course.capacity,
                            )
                        )
                        room_occupied.add((room_name, day, slot))

        return sessions_by_year

    # ------------------------------------------------------------------
    #  VALIDATION (aynı kaldı)
    # ------------------------------------------------------------------

    def validate(self, sessions_by_year: Dict[int, List[Session]]) -> List[Conflict]:
        conflicts: List[Conflict] = []

        by_instructor_time: Dict[tuple, List[Session]] = {}
        daily_theory_hours: Dict[tuple, int] = {}
        theory_seen_for_course: Dict[str, bool] = {}

        for year, sessions in sessions_by_year.items():
            for s in sessions:
                # Friday exam block rule
                if s.day == 4 and s.slot_index in FRIDAY_EXAM_SLOT_INDEXES:
                    conflicts.append(
                        Conflict(
                            "RULE VIOLATION",
                            f"{s.course_code} scheduled in Friday exam block "
                            f"({TIME_SLOTS[s.slot_index]}).",
                        )
                    )

                # Instructor overlaps
                key = (s.instructor, s.day, s.slot_index)
                by_instructor_time.setdefault(key, []).append(s)

                # Daily theory-hours per instructor
                if not s.is_lab:
                    dkey = (s.instructor, s.day)
                    daily_theory_hours[dkey] = daily_theory_hours.get(dkey, 0) + 1

                # Lab before theory (isim üzerinden çok basit kural)
                if s.is_lab:
                    if s.course_code.replace(" Lab", "") not in theory_seen_for_course:
                        conflicts.append(
                            Conflict(
                                "RULE VIOLATION",
                                f"Lab session scheduled before theory for {s.course_code}.",
                            )
                        )
                else:
                    theory_seen_for_course[s.course_code] = True

                # Lab capacity
                if s.is_lab and s.capacity > self.max_lab_capacity:
                    conflicts.append(
                        Conflict(
                            "CAPACITY ISSUE",
                            f"Lab capacity ({s.capacity}) exceeds max allowed "
                            f"({self.max_lab_capacity}) for {s.course_code}.",
                        )
                    )

        # Instructor overlaps
        for (inst, day, slot), sess_list in by_instructor_time.items():
            if len(sess_list) > 1:
                codes = ", ".join(s.course_code for s in sess_list)
                conflicts.append(
                    Conflict(
                        "INSTRUCTOR OVERLAP",
                        f"{inst} scheduled for [{codes}] at the same time "
                        f"({DAYS[day]}, {TIME_SLOTS[slot]}).",
                    )
                )

        # Max theory-hours/day per instructor
        for (inst, day), hours in daily_theory_hours.items():
            if hours > self.max_theory_per_day:
                conflicts.append(
                    Conflict(
                        "RULE VIOLATION",
                        f"{inst} teaches {hours} theory hours on {DAYS[day]} "
                        f"(limit is {self.max_theory_per_day}).",
                    )
                )

        return conflicts

    # ------------------------------------------------------------------
    #  STATISTICS (aynı)
    # ------------------------------------------------------------------

    def compute_stats(self, sessions_by_year: Dict[int, List[Session]]) -> ScheduleStats:
        all_sessions = [s for lst in sessions_by_year.values() for s in lst]
        course_codes = {s.course_code for s in all_sessions}

        theory_hours = sum(1 for s in all_sessions if not s.is_lab)
        lab_hours = sum(1 for s in all_sessions if s.is_lab)

        used_slots: Set[tuple] = {(s.day, s.slot_index) for s in all_sessions}
        total_slots = len(DAYS) * len(TIME_SLOTS)
        utilization = int(len(used_slots) / total_slots * 100) if total_slots else 0

        efficiency = min(100, 60 + utilization // 2)

        return ScheduleStats(
            total_courses=len(course_codes),
            theory_hours=theory_hours,
            lab_hours=lab_hours,
            classroom_utilization=utilization,
            schedule_efficiency=efficiency,
        )
