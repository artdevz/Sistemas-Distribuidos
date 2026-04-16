import grpc
import calc_pb2
import calc_pb2_grpc

def calcular(stub, a, b, op):
    req = calc_pb2.Operacao(a=a, b=b, op=op)
    res = stub.Calcular(req)

    if res.erro:
        print(f"[{op}] Erro: {res.erro}")
    else:
        print(f"{a} {op} {b} = {res.valor:.2f}")

def main():
    with grpc.insecure_channel("localhost:50051") as channel:
        stub = calc_pb2_grpc.CalculadoraStub(channel)

        print("Client gRPC - Calculadora:")
        calcular(stub, 10, 4, "som")
        calcular(stub, 10, 4, "sub")
        calcular(stub, 10, 4, "mul")
        calcular(stub, 10, 4, "div")
        calcular(stub, 10, 0, "div")
        calcular(stub, 10, 4, "pot")

if __name__ == "__main__":
    main()