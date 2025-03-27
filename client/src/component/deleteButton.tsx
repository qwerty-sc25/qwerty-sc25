import { Button } from "@mui/material";

type DeleteButtonProps = {
  onClick: () => void; // 버튼 클릭 시 실행할 함수
};

export default function DeleteButton({ onClick }: DeleteButtonProps) {
  return (
    <Button variant="outlined" color="error" onClick={onClick}>
      삭제
    </Button>
  );
}
