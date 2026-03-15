"""
Kafka Training Demo Script
Hits all Kafka controller endpoints in sequence to demonstrate concepts to trainees.
Make sure the Spring Boot app is running on localhost:8080 before running this.
"""

import requests
import json
import time

BASE = "http://localhost:8080"
TRAINING = f"{BASE}/api/kafka-training"
DEMO     = f"{BASE}/api/kafka-demo"

def section(title):
    print(f"\n{'='*60}")
    print(f"  {title}")
    print('='*60)

def call(method, url, **kwargs):
    try:
        resp = getattr(requests, method)(url, timeout=10, **kwargs)
        print(f"\n[{method.upper()}] {url}")
        print(f"Status : {resp.status_code}")
        print(f"Response: {json.dumps(resp.json(), indent=2)}")
        return resp.json()
    except Exception as e:
        print(f"ERROR calling {url}: {e}")
    return {}

# ── 1. BASICS ────────────────────────────────────────────────
section("1. Kafka Basics")
call("post", f"{TRAINING}/demonstrate-basics")
time.sleep(1)

# ── 2. PRODUCER PATTERNS ─────────────────────────────────────
section("2. Producer Patterns (sync, async, headers)")
call("post", f"{TRAINING}/demonstrate-producers")
time.sleep(1)

# ── 3. CONSUMER PATTERNS ─────────────────────────────────────
section("3. Consumer Patterns")
call("get", f"{TRAINING}/demonstrate-consumers")
time.sleep(1)

# ── 4. EVENT-DRIVEN ARCHITECTURE ─────────────────────────────
section("4. Event-Driven Architecture")
call("post", f"{TRAINING}/demonstrate-event-driven")
time.sleep(1)

# ── 5. ERROR HANDLING ─────────────────────────────────────────
section("5. Error Handling: Retries, DLQ, Circuit Breaker")
call("post", f"{TRAINING}/demonstrate-error-handling")
time.sleep(1)

# ── 6. SPRING CLOUD STREAM ───────────────────────────────────
section("6. Spring Cloud Stream")
call("get", f"{TRAINING}/demonstrate-spring-cloud-stream")
time.sleep(1)

# ── 7. TOPICS & PARTITIONS ───────────────────────────────────
section("7. Topics & Partitions")
call("get", f"{TRAINING}/demonstrate-topics-partitions")
time.sleep(1)

# ── 8. PERFORMANCE ───────────────────────────────────────────
section("8. Performance & Optimization")
call("get", f"{TRAINING}/demonstrate-performance")
time.sleep(1)

# ── 9. MONITORING ────────────────────────────────────────────
section("9. Monitoring & Observability")
call("get", f"{TRAINING}/demonstrate-monitoring")
time.sleep(1)

# ── 10. SEND TRAINING MESSAGE ────────────────────────────────
section("10. Send a Practice Message")
call("post", f"{TRAINING}/send-training-message",
     params={"eventType": "TASK_CREATED", "taskTitle": "Trainee Practice Task"})
time.sleep(1)

# ── 11. STREAMBRIDGE DEMO ────────────────────────────────────
section("11. StreamBridge Demo - Send Events")
call("post", f"{DEMO}/send-test")
time.sleep(0.5)

call("post", f"{DEMO}/send-task-created",
     params={"taskTitle": "Demo Task", "userId": 1, "userName": "trainee"})
time.sleep(0.5)

call("post", f"{DEMO}/send-task-updated",
     params={"taskId": 101, "taskTitle": "Demo Task", "taskStatus": "IN_PROGRESS", "userId": 1, "userName": "trainee"})
time.sleep(0.5)

call("post", f"{DEMO}/send-task-completed",
     params={"taskId": 101, "taskTitle": "Demo Task", "userId": 1, "userName": "trainee"})
time.sleep(1)

# ── 12. SEARCH CONSUMED MESSAGES ─────────────────────────────
section("12. Search Consumed Messages")
call("get", f"{DEMO}/messages/search", params={"keyword": "trainee"})
time.sleep(0.5)
call("get", f"{DEMO}/messages/search")  # all messages

# ── 13. QUIZ ─────────────────────────────────────────────────
section("13. Check Understanding (Quiz)")
call("get", f"{TRAINING}/check-understanding")

# ── 14. SUMMARY ──────────────────────────────────────────────
section("14. Training Summary")
call("get", f"{TRAINING}/summary")

print(f"\n{'='*60}")
print("  Demo complete! Check application logs for consumer output.")
print('='*60)
