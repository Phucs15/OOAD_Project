package Functions.addFuctions;

public class Sorting {
    public static void quickSort(Object[][] data, int low, int high, boolean ascending, int columnIndex) {
        if (low < high) {
            int pi = partition(data, low, high, ascending, columnIndex);

            quickSort(data, low, pi - 1, ascending, columnIndex);
            quickSort(data, pi + 1, high, ascending, columnIndex);
        }
    }

    private static int partition(Object[][] data, int low, int high, boolean ascending, int columnIndex) {
        Object pivot = data[high][columnIndex];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (data[j][columnIndex] == null) continue;

            boolean condition;
            if (isNumeric(data[j][columnIndex]) && isNumeric(pivot)) {
                double num1 = Double.parseDouble(data[j][columnIndex].toString());
                double num2 = Double.parseDouble(pivot.toString());
                condition = ascending ? num1 <= num2 : num1 >= num2;
            } else {
                condition = ascending
                        ? data[j][columnIndex].toString().compareTo(pivot.toString()) <= 0
                        : data[j][columnIndex].toString().compareTo(pivot.toString()) >= 0;
            }

            if (condition) {
                i++;
                Object[] temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
        }

        Object[] objecttemp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = objecttemp;

        return i + 1;
    }

    private static boolean isNumeric(Object obj) {
        if (obj == null) return false;
        try {
           Double.parseDouble(obj.toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
