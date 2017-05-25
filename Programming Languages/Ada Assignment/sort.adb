package body Sort is
  procedure Quicksort(Low : Integer; High : Integer) is
    I : Integer := Low;
    J : Integer := High;
    Temp : Integer;
    M : Integer := Low + (High - Low) / 2;
    Pivot : Integer := A(M);

  begin
    if Low >= High then
      return;

    else
      while I <= J loop
        while A(I) < Pivot loop
          I := I + 1;
        end loop;

        while A(J) > Pivot loop
          J := J - 1;
        end loop;

        if I <= J then
          Temp := A(I);
          A(I) := A(J);
          A(J) := Temp;
          I := I + 1;
          J := J - 1;
        end if;
      end loop;

      Start(Low, High, I, J);
    end if;
  end Quicksort;

  procedure Start(Low : Integer; High : Integer; I : Integer; J : Integer) is
    task left;
    task right;

    task body left is
    begin
      Quicksort(Low, J);
    end left;

    task body right is
    begin
      Quicksort(I, High);
    end right;
  begin
    null;
  end Start;
end Sort;
