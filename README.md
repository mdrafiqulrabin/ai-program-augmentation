# AI-Program-Augmentation
Project: 2020SU-COSC6368-Artificial Intelligence \
Task: Apply Data Augmentation in Program Domain.

- - -

# Library:
- Python 3.7.4
- javaparser 3.14.5

- - -

# Program Augmentation:

Given input and output path, create jar and execute jar.
  * input = Input directory to the original programs.
  * output = Output directory to the augmented programs.

  ```
  $ cd augment/
  $ source run.sh
  $ java -jar JavaAugmentation/target/jar/JavaAugmentation.jar $input $output
  ```

## Types:

  * Function Augmentation
  * Statement Augmentation
  * Loop Augmentation
  * Switch Augmentation
  * Binary Augmentation

## Example:

| <img src="https://github.com/mdrafiqulrabin/ai-program-augmentation/blob/master/augment/examples/function.png" alt="Function Augmentation" width="420"/>  |  <img src="https://github.com/mdrafiqulrabin/ai-program-augmentation/blob/master/augment/examples/statement.png" alt="Statement Augmentation" width="420"/> |
:-------------------------:|:-------------------------:

( An example of Function Augmentation (left) and Statement Augmentation (right). Check [here](https://github.com/mdrafiqulrabin/ai-program-augmentation/blob/master/augment/) for other types. )

# Results

| Dataset   | Partition  | Best Epoch | Accuracy (%)
| --------- | ---------- | ---------- | -----------
| Original  | Validation | 7          | 87.11
| Augmented | Validation | 8          | 90.70
| Original  | Test       | 7          | 90.81
| Augmented | Test       | 8          | 93.35

# References:

•	code2vec: https://github.com/tech-srl/code2vec \
•	SA: https://github.com/bdqnghi/ggnn.tensorflow#code-classification \
•	javaparser: https://github.com/javaparser/javaparser 
