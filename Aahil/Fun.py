import cv2 
import mediapipe as mp
from OpenGL.GL import *
from OpenGL.GLUT import *
from OpenGL.GLU import * 
      
# MediaPipe setup 
mp_hands = mp.solutions.hands
hands = mp_hands.Hands(static_image_mode=False, max_num_hands=1, min_detection_confidence=0.5)

# Rotation variables
x_rotation = 0 
y_rotation = 0

# OpenGL cube vertices
vertices = [ 
    [1, 1, -1],
    [1, -1, -1],
    [-1, -1, -1],
    [-1, 1, -1],
    [1, 1, 1],
    [1, -1, 1],
    [-1, -1, 1],
    [-1, 1, 1],
]
edges = [
    (0, 1), (1, 2), (2, 3), (3, 0),
    (4, 5), (5, 6), (6, 7), (7, 4),
    (0, 4), (1, 5), (2, 6), (3, 7),
]

# Draw the cube
def draw_cube():
    glBegin(GL_LINES)
    for edge in edges:
        for vertex in edge:
            glVertex3fv(vertices[vertex])
    glEnd()

# OpenGL display function
def display():
    global x_rotation, y_rotation
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    glLoadIdentity()
    glTranslatef(0.0, 0.0, -5)
    glRotatef(x_rotation, 1, 0, 0)
    glRotatef(y_rotation, 0, 1, 0)
    draw_cube()
    glutSwapBuffers()

# Hand tracking and rotation logic
def track_hands():
    global x_rotation, y_rotation
    cap = cv2.VideoCapture(0)
    prev_x, prev_y = None, None

    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        frame = cv2.flip(frame, 1)
        rgb_frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        results = hands.process(rgb_frame)

        if results.multi_hand_landmarks:
            for hand_landmarks in results.multi_hand_landmarks:
                # Get wrist landmark
                wrist = hand_landmarks.landmark[mp_hands.HandLandmark.WRIST]
                x, y = int(wrist.x * frame.shape[1]), int(wrist.y * frame.shape[0])

                # Calculate movement
                if prev_x is not None and prev_y is not None:
                    dx = x - prev_x
                    dy = y - prev_y
                    x_rotation += dy * 0.5
                    y_rotation += dx * 0.5

                prev_x, prev_y = x, y

        # Display webcam feed
        cv2.imshow("Hand Tracking", frame)
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

# Initialize OpenGL and run
def main():
    glutInit()
    glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE | GLUT_DEPTH)
    glutInitWindowSize(800, 600)
    glutCreateWindow("3D Cube with Hand Tracking")
    glEnable(GL_DEPTH_TEST)
    glutDisplayFunc(display)
    glutIdleFunc(display)
    track_hands()
    glutMainLoop()

if __name__ == "__main__":
    main()
