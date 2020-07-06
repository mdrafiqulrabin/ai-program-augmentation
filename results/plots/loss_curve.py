import os
import pathlib

import matplotlib.pyplot as plt
import pandas as pd

log_path = "../training/{}/"
partitions = ["train", "val"]
colors = {0: "green", 1: "black", 2: "blue"}
model_epochs = {"original": "7", "augmented": "38"}
max_epoch = 50

for model_name, best_epoch in model_epochs.items():
    losses = []
    for partition in partitions:
        losses_avg = []
        for ep in range(1, max_epoch + 1):
            log_file = log_path.format(model_name) + "{}_log_epoch_{}.txt".format(partition, ep)
            print("log_file : ", log_file)
            df = pd.read_csv(log_file, names=["path", "original", "prediction", "noscore", "loss"])
            loss_ep = df["loss"].tolist()
            losses_avg.append(sum(loss_ep) * 1.0 / len(loss_ep))
        losses.append(losses_avg)

    plt.figure()
    plt.xlabel("Epochs")
    plt.ylabel("Average cross-entropy loss")
    plt.title("The training and validation losses")
    x = [i + 1 for i in range(max_epoch)]
    for i, y in enumerate(losses):
        plt.plot(x, y, label="{} set".format(partitions[i]), color=colors[i])
    plt.legend()
    plot_file = 'loss_curve_{}.png'.format(model_name)
    pathlib.Path(os.path.dirname(plot_file)).mkdir(parents=True, exist_ok=True)
    plt.savefig(plot_file, dpi=400)
    plt.show()
