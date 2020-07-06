import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import roc_curve, auc
from keras.utils import np_utils

pred_path = "../prediction/{}/"
model_epochs = {"original": "7", "augmented": "38"}
partitions = ["train", "val", "test"]
max_epoch = 50
n_classes = 10
class_dict = {"bubble": 0, "bucket": 1, "heap": 2, "insertion": 3, "merge": 4, "quick": 5,
              "radix": 6, "selection": 7, "shell": 8, "topological": 9}
class_colors = ['green', 'blue', 'red', 'black', 'grey', 'pink', 'yellow', 'orange', 'silver', 'maroon']


def draw_roc_curve(t_model_name, t_y_test, t_y_pred):
    t_y_test = np_utils.to_categorical(t_y_test, num_classes=n_classes)
    t_y_pred = np_utils.to_categorical(t_y_pred, num_classes=n_classes)

    fpr, tpr, roc_auc = dict(), dict(), dict()
    for i in range(n_classes):
        fpr[i], tpr[i], _ = roc_curve(t_y_test[:, i], t_y_pred[:, i])
        roc_auc[i] = auc(fpr[i], tpr[i])

    for i in range(n_classes):
        plt.plot(fpr[i], tpr[i], color=class_colors[i], lw=1,
                 label='Class {0} (AUC = {1:0.2f})'.format(i, roc_auc[i]))

    plt.plot([0, 1], [0, 1], color='navy', lw=1, linestyle='--')
    plt.xlim([-0.05, 1.0])
    plt.ylim([0.0, 1.05])
    plt.xlabel('False Positive Rate')
    plt.ylabel('True Positive Rate')
    plt.title('Receiver Operating Characteristic (ROC)')
    plt.legend(loc="lower right")
    plt.savefig('roc_curve_{}.png'.format(t_model_name), dpi=400)
    plt.show()


for model_name, best_epoch in model_epochs.items():
    log_file = pred_path.format(model_name) + "test_log_epoch_{}.txt".format(best_epoch)
    print("log_file : ", log_file)
    df = pd.read_csv(log_file, names=["path", "original", "prediction", "noscore", "loss"])
    y_test = df["original"].tolist()
    y_test = [class_dict[v] for v in y_test]
    y_pred = df["prediction"].tolist()
    y_pred = [class_dict[v] for v in y_pred]
    draw_roc_curve(model_name, y_test, y_pred)
