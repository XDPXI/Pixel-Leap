import os
import shutil
import zipfile

def move_and_extract_jars(source_folder, target_folder):
    """
    Move JAR files from subfolders of `source_folder` to `target_folder`,
    excluding files with '-javadoc' or '-sources' in their names, and extract them.

    :param source_folder: Path to the folder named "import"
    :param target_folder: Path to the folder named "export"
    """
    if not os.path.exists(target_folder):
        os.makedirs(target_folder)

    for root, _, files in os.walk(source_folder):
        for file in files:
            if file.endswith(".jar") and "-javadoc" not in file and "-sources" not in file:
                source_path = os.path.join(root, file)
                target_path = os.path.join(target_folder, file)

                print(f"Moving {source_path} to {target_path}")
                shutil.move(source_path, target_path)

                # Extract the JAR file
                try:
                    with zipfile.ZipFile(target_path, 'r') as jar_file:
                        print(f"Extracting {target_path} to {target_folder}")
                        jar_file.extractall(target_folder)
                except zipfile.BadZipFile:
                    print(f"Error: {target_path} is not a valid zip file.")

if __name__ == "__main__":
    source_dir = "import"
    target_dir = "export"

    move_and_extract_jars(source_dir, target_dir)
    print("All eligible JAR files have been moved and extracted.")