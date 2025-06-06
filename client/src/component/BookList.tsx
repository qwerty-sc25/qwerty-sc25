import {
  Card,
  CardMedia,
  Container,
  LinearProgress,
  Skeleton,
  Stack,
  Typography,
} from "@mui/material";
import LinkCardActionArea from "./LinkCardActionArea";
import { BookMetadata } from "../types/book";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import PageNavigation from "./PageNavigation";
import { JSX, useState } from "react";
import API_CLIENT from "../api/api";

export enum BookListKind {
  ALL_BOOK = "ALL_BOOK",
  PURCHASED_BOOK = "PURCHASED_BOOK",
}

export default function BookList(props: {
  size: "small" | "large";
  action?: JSX.Element;
  kind?: BookListKind;
  title: string;
  searchTitle?: string;
}) {
  const { size, action, kind: kind_, title, searchTitle } = props;
  const [sort, _setSort] = useState<string[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const pageSize = size === "small" ? 5 : 25;
  const kind = kind_ ?? BookListKind.ALL_BOOK;

  const { data: books } = useQuery({
    queryKey: [kind, page, sort, pageSize, searchTitle],
    queryFn: async () => {
      const response = await getFetchFunction(kind)({
        page,
        size: pageSize,
        sort,
        title: searchTitle,
      });
      if (response.isSuccessful) {
        setTotalPages(response.data.totalPages!);
        return response.data.content! as (BookMetadata | undefined)[];
      }
      throw new Error(response.errorMessage);
    },
    initialData: new Array(pageSize).fill(undefined) as (
      | BookMetadata
      | undefined
    )[],
    placeholderData: keepPreviousData,
  });

  return (
    <Container>
      <Stack spacing={1}>
        <Stack
          direction="row"
          justifyContent="space-between"
          alignItems="center"
        >
          <Typography variant="h4">{title}</Typography>
          {action}
        </Stack>
        <PageNavigation
          pageZeroBased={page}
          setPage={setPage}
          totalPages={totalPages}
        />
        <Stack spacing={2}>
          {books.map((book) => (
            <BookListItem key={book?.id ?? Math.random()} book={book} />
          ))}
        </Stack>
        <PageNavigation
          pageZeroBased={page}
          setPage={setPage}
          totalPages={totalPages}
        />
      </Stack>
    </Container>
  );
}

function BookListItem(props: { book?: BookMetadata }) {
  const { book } = props;

  const { data: readProgress } = useQuery({
    queryKey: ["bookReadProgress", book?.id],
    queryFn: async () => {
      if (!book) return 0;
      const response = await API_CLIENT.readingProgressController.getMyProgress(
        book.id
      );
      if (!response.isSuccessful) {
        throw new Error(response.errorMessage);
      }
      return response.data.percentage!;
    },
    initialData: 0,
    enabled: book?.isPurchased,
  });

  if (!book) {
    return (
      <Card variant="outlined" sx={{ padding: 2 }}>
        <Stack spacing={1} direction={"row"}>
          <Skeleton variant="rectangular" width={128} height={160} />
          <Stack flexGrow={1} spacing={1}>
            <Skeleton variant="text" width="60%" height={32} />
            <Skeleton variant="text" width="40%" height={24} />
            <Skeleton variant="text" width="30%" height={20} />
          </Stack>
        </Stack>
      </Card>
    );
  }

  return (
    <Card variant="outlined" key={book.id}>
      <LinearProgress value={readProgress} variant="determinate" />
      <Stack
        spacing={1}
        direction={{
          xs: "column",
          sm: "row",
        }}
        alignItems={"center"}
        sx={{ padding: 2 }}
      >
        <LinkCardActionArea
          sx={{ width: 128, height: 160 }}
          to="/books/$bookId"
          params={{ bookId: book.id }}
        >
          <CardMedia
            image={book.bookCoverImageURL || "https://picsum.photos/128/160"}
            sx={{ width: 128, height: 160 }}
          />
        </LinkCardActionArea>
        <Stack flexGrow={1} spacing={1}>
          <LinkCardActionArea to="/books/$bookId" params={{ bookId: book.id }}>
            <Typography variant="h5">{book.title}</Typography>
          </LinkCardActionArea>
          <LinkCardActionArea to="/books/$bookId" params={{ bookId: book.id }}>
            <Typography variant="body2">{book.author}</Typography>
          </LinkCardActionArea>
          <Typography variant="body2" color="textSecondary">
            {book.description.length > 50
              ? book.description.slice(0, 128) + "..."
              : book.description}
          </Typography>
          <Stack
            direction="row"
            justifyContent="flex-end"
            alignItems={"center"}
            spacing={2}
          >
            <Typography variant="body2" color="textSecondary">
              {(book.size / 1024 / 1024).toFixed(1)} MiB
            </Typography>
            <Typography variant="body1" color="primary">
              {book.price.toLocaleString()}원
            </Typography>
          </Stack>
        </Stack>
      </Stack>
    </Card>
  );
}

function getFetchFunction(groupType: BookListKind) {
  switch (groupType) {
    case BookListKind.ALL_BOOK:
      return API_CLIENT.ebookController.getBooks;
    case BookListKind.PURCHASED_BOOK:
      return API_CLIENT.ebookPurchaseController.getMyBooks;
    default:
      throw new Error("Invalid group type");
  }
}
