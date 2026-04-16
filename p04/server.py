import grpc
from concurrent import futures
import calc_pb2
import calc_pb2_grpc

class CalculadoraServicer(calc_pb2_grpc.CalculadoraServicer):

    def Calcular(self, request, context):
        a, b, op = request.a, request.b, request.op
        try:
            if op == "som": resultado = a + b
            elif op == "sub": resultado = a - b
            elif op == "mul": resultado = a * b
            elif op == "div":
                if b == 0:
                    return calc_pb2.Resultado(erro="Divisão por zero")
                resultado = a / b
            else:
                return calc_pb2.Resultado(erro=f"Operação desconhecida: {op}")
            return calc_pb2.Resultado(valor=resultado)
        except Exception as e:
            return calc_pb2.Resultado(erro=str(e))

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(
        max_workers=10))
    calc_pb2_grpc.add_CalculadoraServicer_to_server(
        CalculadoraServicer(), server
    )
    server.add_insecure_port("[::]:50051")
    server.start()
    print("Servidor gRPC aguardando conexões na porta 50051...")
    server.wait_for_termination()

if __name__ == "__main__":
    serve()