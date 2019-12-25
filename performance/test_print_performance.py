# python3 -m pip install locustio - See https://docs.locust.io/en/stable/installation.html
# Running the tests with web ui
# locust -f this_file.py
# Or to run it without web ui for 20(secs) with 10 users and 2 users to spawn every second
# locust -f locust_test.py --no-web -c 10 -r 2 --run-time 20s

import base64
import random
import string
import uuid
from locust import HttpLocust, TaskSequence, seq_task
from locust.wait_time import between


def random_uuid():
    return str(uuid.uuid4())


def random_str(length=10, with_punctuation=False):
    selection = string.ascii_letters + string.digits
    selection = selection + string.punctuation if with_punctuation else selection
    return ''.join(random.choice(selection) for i in range(length))


def random_int(min=0, max=100):
    return random.randint(min, max)


def str_to_bytes(input_str: str):
    return bytes(input_str, encoding="utf-8")


def bytes_to_str(input_bytes: bytes):
    return input_bytes.decode(encoding="utf-8")


def str_to_base64_encoded_bytes(input_str) -> bytes:
    return base64.standard_b64encode(str_to_bytes(input_str))


class ApiTestSteps(TaskSequence):

    print_location: string

    @seq_task(1)
    # @task(10) # To run this test 10 times
    def save_print(self):
        # Group requests if it contains variable params
        # self.client.get("/api?id=%i" % i, name="/api?id=[id]")
        document_code_bytes = str_to_base64_encoded_bytes(random_str(100))
        response = self.client.post("http://127.0.0.1:8080/prints",
                                    headers={
                                        "User-Agent": "python-requests/2.22.0",
                                        "Content-Type": "application/json"
                                    },
                                    json={"document": bytes_to_str(document_code_bytes)})
        self.print_location = response.headers["Location"]
        print(f'Response HTTP Status Code: {response.status_code}')

    # PrintRider: Get Print

    @seq_task(2)
    # @task(10) # To run this test 10 times
    def get_print(self):
        # Group requests if it contains variable params
        # self.client.get("/api?id=%i" % i, name="/api?id=[id]")
        response = self.client.get(
            self.print_location,
            name="/prints/{id}",
            headers={
                "User-Agent": "python-requests/2.22.0",
                "Accept-Encoding": "gzip, deflate",
                "Accept": "text/html",
                "Connection": "keep-alive",
                "X-Shared": "Common-Header-Value"
            },
        )
        print(f'Response HTTP Status Code: {response.status_code}')


class ApiUser(HttpLocust):
    task_set = ApiTestSteps
    host = "localhost"
    wait_time = between(1, 2)
