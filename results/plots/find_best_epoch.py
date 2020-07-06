import numpy as np

log_path = "../logs/train_{}.txt"
model_names = ["original", "augmented"]

for model_name in model_names:
    print("model_name = ", model_name)
    model_txt = log_path.format(model_name)
    top_acc = []
    with open(model_txt, "r") as f_model:
        for line in f_model:
            if "epochs -- top10_acc" in line:
                acc = line.split()[8][1:]
                top_acc.append(float(acc))
    print("All epochs: {}".format(top_acc))
    max_idx = int(np.argmax(top_acc))
    print("Best epoch - {}: {}".format(max_idx+1, top_acc[max_idx]))
    print("\n")
