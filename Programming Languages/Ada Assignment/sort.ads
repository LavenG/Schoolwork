package Sort is
  Size : Integer := 29;
  subtype Int_Range is Integer range 0 .. 1000;
  type arrayType is array (0 .. Size) of Int_Range;
  A : arrayType;

  procedure Quicksort(Low : Integer; High : Integer);
  procedure Start(Low : Integer; High : Integer; I : Integer; J : Integer);
end Sort;
