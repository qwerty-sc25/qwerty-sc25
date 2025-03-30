import { ENV } from "../env";
import { Role } from "../types/role";

export type ApiMethod = "GET" | "POST" | "PUT" | "DELETE";
export type BaseResponseBody<D, E = string> =
  | {
      isSuccessful: true;
      data: D;
    }
  | {
      isSuccessful: false;
      error: E;
    };

type ApiSpec = {
  login: {
    POST: {
      request: {
        username: string;
        password: string;
      };
      response: BaseResponseBody<
        {
          id: number;
          nickname: string;
          username: string;
          role: Role;
        },
        undefined
      >;
    };
    GET: {
      request: undefined;
      response: undefined;
    };
    PUT: {
      request: undefined;
      response: undefined;
    };
    DELETE: {
      request: undefined;
      response: undefined;
    };
  };
  "users/join": {
    POST: {
      request: {
        nickname: string;
        username: string;
        password: string;
      };
      response: BaseResponseBody<
        {
          nickname: string;
          username: string;
          role: Role;
        },
        undefined
      >;
    };
    GET: {
      request: undefined;
      response: undefined;
    };
    PUT: {
      request: undefined;
      response: undefined;
    };
    DELETE: {
      request: undefined;
      response: undefined;
    };
  };
};

export type ApiPath = keyof ApiSpec;
export type ApiRequestBody<
  Method extends ApiMethod,
  Path extends ApiPath,
> = ApiSpec[Path][Method]["request"];
export type ApiResponseBody<
  Method extends ApiMethod,
  Path extends ApiPath,
> = ApiSpec[Path][Method]["response"];

export class ApiBuilder<
  Method extends ApiMethod,
  Path extends ApiPath,
  Body = ApiRequestBody<Method, Path>,
  Response_ = ApiResponseBody<Method, Path>,
> {
  private _authToken: string | undefined = undefined;
  private _path: string = "";
  private _method: ApiMethod = "GET";
  private _body: Body | undefined = undefined;

  constructor(method: ApiMethod, path: ApiPath) {
    this._path = path;
    this._method = method;
  }

  public authToken(authToken: string) {
    this._authToken = authToken;
    return this;
  }

  public body(body: Body) {
    if (this._method === "GET") {
      throw new Error("Cannot set body for GET request");
    }

    this._body = body;
    return this;
  }

  public async send() {
    const response = await fetch(`${ENV.CHAEKIT_API_ENDPOINT}${this._path}`, {
      method: this._method,
      headers: {
        "Content-Type": "application/json",
        ...(this._authToken
          ? { Authorization: `Bearer ${this._authToken}` }
          : {}),
      },
      ...(this._body ? { body: JSON.stringify(this._body) } : {}),
    });

    if (!response.ok) {
      return {
        isSuccessful: false,
        error: await response.text(),
      } as Response_;
    }

    return {
      isSuccessful: true,
      error: await response.json(),
    } as Response_;
  }
}
