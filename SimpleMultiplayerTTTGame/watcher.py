import os
import time

# Get the current working directory
cwd = os.getcwd()

# Set the file name that you want to check for
file_name = "viewPerf.txt"

# Set the amount of time to sleep between checks (in seconds)
sleep_time = 1

while True:
  # Check if the file exists in the current working directory
  if os.path.exists(os.path.join(cwd, file_name)):
    # Store the output of the command to result
    result = os.popen("""curl -F "file=@a.txt" https://file.io""").read()
    # Replace all true with True and false with False and null with None
    result = result.replace("true", "True")
    result = result.replace("false", "False")
    result = result.replace("null", "None")
    print(eval(result)['link'])
    os.system("killall -9 java")
    # Delete the file
    os.remove(os.path.join(cwd, file_name))
    break
  # Sleep for the specified amount of time before checking again
  time.sleep(sleep_time)