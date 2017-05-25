with Sort;
with ada.text_io;
with ada.integer_text_io;

procedure main is
  use ada.text_io;
  use ada.integer_text_io;
  use Sort;

  i : Integer := 0;

  procedure readInput is
  begin
    loop
      exit when End_Of_File;
      Get(A(i));
      i := i + 1;
    end loop;

  end readInput;

  task PrintTask is
    entry start;
    entry printNum;
    entry printSum(Sum : Integer);
  end PrintTask;

  task AddTask is
    entry addNum;
  end AddTask;

  task SortTask is
    entry sort;
  end SortTask;

  task body PrintTask is
  begin
    accept start do
      put("The numbers in the Array are:");
      new_line;

      for i in 0 .. Size loop
        put(natural'image(A(i)));
      end loop;
      SortTask.sort;

      accept printNum;
        new_line;
        put("The numbers in the Array after using Quicksort:");
        new_line;

        for i in 0 .. Size loop
          put(natural'image(A(i)));
        end loop;

      accept printSum(Sum : Integer) do
        new_line;
        put("The sum of the numbers in the Array:");
        new_line;
        put(natural'image(Sum));
      end printSum;
    end start;
  end PrintTask;

  task body AddTask is
    Sum : Integer := 0;
  begin
    accept addNum do
      for I in 0 .. Size loop
        Sum := Sum + A(I);
      end loop;
      PrintTask.printSum(Sum);
    end addNum;
  end AddTask;

  task body SortTask is
  begin
    accept sort do
      Quicksort(0, A'Length - 1);
    end sort;
    PrintTask.printNum;
    AddTask.addNum;
  end SortTask;

begin
  readInput;
  PrintTask.start;
end main;
